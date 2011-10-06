package eu.uberdust.nodeflasher;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import de.uniluebeck.itm.wisebed.cmdlineclient.jobs.JobResult;
import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClient;
import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClientListener;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.WSNAsyncWrapper;
import eu.wisebed.api.rs.*;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.api.snaa.AuthenticationExceptionException;
import eu.wisebed.api.snaa.AuthenticationTriple;
import eu.wisebed.api.snaa.SNAA;
import eu.wisebed.api.snaa.SNAAExceptionException;
import eu.wisebed.api.wsn.WSN;
import eu.wisebed.testbed.api.rs.RSServiceHelper;
import eu.wisebed.testbed.api.snaa.helpers.SNAAServiceHelper;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


// Import log4j classes.

public class nodeFlasherJob implements Job {

    public static final int INTERVAL = 60;
    private static final int MINUTES_BEFORE = 40;
    private static final int MINUTES_AFTER = 60;

    private SNAA authenticationSystem;
    private RS reservationSystem;
    private SessionManagement sessionManagement;
    private List urnPrefixes;
    private List usernames;
    private List passwords;
    private List secretAuthenticationKeys;
    private String pccHost;
    private int pccPort;

    private String imagePath;
    private Properties properties;
    private static final Logger log = Logger.getLogger(nodeFlasherJob.class);

    public nodeFlasherJob() {

    }


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" |=== Starting a new nodeFlasher");
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

        log.setLevel(Level.INFO);

        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("nodeFlasher.properties"));
        } catch (Exception e) {
            log.error("|*** No properties file found! nodeFlasher.properties not found!");
            return;
        }


        // Authentication credentials and other relevant information used again and again as method parameters

        Splitter csvSplitter = Splitter.on(",").trimResults().omitEmptyStrings();

        urnPrefixes = Lists.newArrayList(csvSplitter.split(properties.getProperty("testbed.urnprefixes")));

        String passfilename = properties.getProperty("testbed.passwords");
        try {
            final Properties passproperties = new Properties();
            passproperties.load(new FileInputStream(passfilename));
            usernames = Lists.newArrayList(csvSplitter.split(passproperties.getProperty("testbed.usernames")));
            passwords = Lists.newArrayList(csvSplitter.split(passproperties.getProperty("testbed.passwords")));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        pccHost = properties.getProperty("testbed.protobuf.hostname");
        pccPort = Integer.parseInt(properties.getProperty("testbed.protobuf.port"));
        Preconditions.checkArgument(
                urnPrefixes.size() == usernames.size() && usernames.size() == passwords.size(),
                "The list of URN prefixes must have the same length as the list of usernames and the list of passwords"
        );

        // Endpoint URLs of Authentication (SNAA), Reservation (RS) and Experimentation (iWSN) services
        String snaaEndpointURL = properties.getProperty("testbed.snaa.endpointurl");
        String rsEndpointURL = properties.getProperty("testbed.rs.endpointurl");
        String sessionManagementEndpointURL = properties.getProperty("testbed.sm.endpointurl");


        // Retrieve Java proxies of the endpoint URLs above
        authenticationSystem = SNAAServiceHelper.getSNAAService(snaaEndpointURL);
        reservationSystem = RSServiceHelper.getRSService(rsEndpointURL);
        sessionManagement = WSNServiceHelper.getSessionManagementService(sessionManagementEndpointURL);


        // build argument types
        List credentialsList = new ArrayList();
        for (int i = 0; i < urnPrefixes.size(); i++) {

            AuthenticationTriple credentials = new AuthenticationTriple();

            credentials.setUrnPrefix((String) urnPrefixes.get(i));
            credentials.setUsername((String) usernames.get(i));
            credentials.setPassword((String) passwords.get(i));

            credentialsList.add(credentials);
        }

        // do the authentication
        log.info("|   Authenticating to SNAA...");
        try {
            secretAuthenticationKeys = authenticationSystem.authenticate(credentialsList);
        } catch (AuthenticationExceptionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SNAAExceptionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        log.info("|   Successfully authenticated!");


        checkReservations(properties.getProperty("nodes.isense").split(","), "isense");
        checkReservations(properties.getProperty("nodes.telosb").split(","), "telosb");


    }

    private void checkReservations(String[] nodes, String type) {

        imagePath = properties.getProperty("image." + type);
        log.info("|==  Checking for " + type + " nodes");
        try {

            GregorianCalendar cal = new GregorianCalendar();
            long from = (new Date()).getTime();
            cal.setTimeInMillis(from);
            XMLGregorianCalendar timeFrom;

            timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            from = (new Date()).getTime() + MINUTES_AFTER * 60 * 1000;
            cal.setTimeInMillis(from);
            XMLGregorianCalendar timeTo;
            timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            List futurereservationlist;

            futurereservationlist = reservationSystem.getReservations(timeFrom, timeTo);


            for (int i = 0; i < futurereservationlist.size(); i++) {
                PublicReservationData a = (PublicReservationData) futurereservationlist.get(i);

                boolean isRelevant = false;
                for (String nodeurn : nodes) {
                    if (a.getNodeURNs().toString().contains(nodeurn)) {
                        isRelevant = true;
                    }
                }
                if (!isRelevant) {
                    futurereservationlist.remove(i);
                    //move back to the next reservation which is now current i
                    i--;
                }
            }


            cal = new GregorianCalendar();
            from = (new Date()).getTime() - MINUTES_BEFORE * 60 * 1000;
            cal.setTimeInMillis(from);


            timeFrom = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            from = (new Date()).getTime();
            cal.setTimeInMillis(from);

            timeTo = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            List previousreservationlist;
            previousreservationlist = reservationSystem.getReservations(timeFrom, timeTo);

            for (int i = 0; i < previousreservationlist.size(); i++) {
                PublicReservationData a = (PublicReservationData) previousreservationlist.get(i);
                boolean isRelevant = false;
                for (String nodeurn : nodes) {
                    if (a.getNodeURNs().toString().contains(nodeurn)) {
                        isRelevant = true;
                    }
                }
                if (!isRelevant) {
                    previousreservationlist.remove(i);
                    //move back to the next reservation which is now current i
                    i--;
                }
            }


            if (futurereservationlist.size() == 0) {
                if (previousreservationlist.size() != 0) {
                    log.info("|== Need to reFlash the " + type + " nodes.");
                    reFlash(nodes);
                } else {
                    log.info("|=  No reservations found, No need to reFlash the " + type + " nodes.");
                }
            } else {
                log.info("|=  Pending Reservation, Cannot reFlash the " + type + " nodes now.");
            }


        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

        } catch (RSExceptionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private String reserveNodes(String[] nodes) {
        // retrieve the node URNs of all iSense nodes

        log.info("|+   Trying to reserve " + nodes.length + " nodes");

        List nodeURNsToReserve = Lists.newArrayList(nodes);
        // create reservation request data to reserve all iSense nodes for 10 minutes
        ConfidentialReservationData reservationData = BeanShellHelper.generateConfidentialReservationData(
                nodeURNsToReserve, new Date(System.currentTimeMillis()), 4, TimeUnit.MINUTES,
                urnPrefixes, usernames);

        List secretReservationKeys = null;
        try {
            secretReservationKeys = reservationSystem.makeReservation(
                    BeanShellHelper.copySnaaToRs(secretAuthenticationKeys),
                    reservationData
            );
        } catch (AuthorizationExceptionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RSExceptionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ReservervationConflictExceptionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        log.info("|+   Successfully reserved " + nodeURNsToReserve.size() + " nodes");
        log.info("|+   Reservation Key(s): " + BeanShellHelper.toString(secretReservationKeys));

        return BeanShellHelper.toString(secretReservationKeys);

    }

    private void reFlash(String[] nodes) {

        try {


            String reskey = reserveNodes(nodes);

            String wsnEndpointURL = null;


            wsnEndpointURL = sessionManagement.getInstance(BeanShellHelper.parseSecretReservationKeys(reskey), "NONE");


            log.info("|+   Got a WSN instance URL, endpoint is: " + wsnEndpointURL);
            WSN wsnService = WSNServiceHelper.getWSNService(wsnEndpointURL);
            final WSNAsyncWrapper wsn = WSNAsyncWrapper.of(wsnService);

            ProtobufControllerClient pcc = ProtobufControllerClient.create(pccHost, pccPort, BeanShellHelper.parseSecretReservationKeys(reskey));
            pcc.addListener(new ProtobufControllerClientListener() {
                public void receive(List msg) {
                    // nothing to do
                }

                public void receiveStatus(List requestStatuses) {
                    wsn.receive(requestStatuses);
                }

                public void receiveNotification(List msgs) {
                    for (Object msg : msgs) {
                        log.info(msg);
                    }
                }

                public void experimentEnded() {
                    log.debug("|+   Experiment ended");
                    System.exit(0);
                }

                public void onConnectionEstablished() {
                    log.debug("|+   Connection established.");
                }

                public void onConnectionClosed() {
                    log.debug("|+   Connection closed.");
                }
            });
            pcc.connect();

            // retrieve reserved node URNs from testbed
            List nodeURNsToFlash = Lists.newArrayList(nodes);

            log.info("|+   Flashing nodes...");


            List programIndices;
            List programs;


            // flash isense nodes
            programIndices = new ArrayList();
            programs = new ArrayList();
            for (Object aNodeURNsToFlash : nodeURNsToFlash) {
                programIndices.add(0);
            }


            programs.add(BeanShellHelper.readProgram(
                    imagePath,
                    "",
                    "",
                    "iSense",
                    "1.0"
            ));


            Future flashFuture = wsn.flashPrograms(nodeURNsToFlash, programIndices, programs, 3, TimeUnit.MINUTES);
            JobResult flashJobResult;

            flashJobResult = (JobResult) flashFuture.get();

            log.info("" + flashJobResult);
            if (flashJobResult.getSuccessPercent() < 100) {
                log.info("|*   Not all nodes could be flashed. Exiting");
            }


            log.info("|+   Closing connection...");
            pcc.disconnect();

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
