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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class PeriodicFlasherJob implements Job {

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
    private static final Logger log = Logger.getLogger(PeriodicFlasherJob.class);

    public PeriodicFlasherJob() {

    }


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" |=== Starting a new PeriodicFlasherJob");
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

        final Splitter csvSplitter = Splitter.on(",").trimResults().omitEmptyStrings();

        urnPrefixes = Lists.newArrayList(csvSplitter.split(properties.getProperty("testbed.urnprefixes")));

        String passfilename = properties.getProperty("testbed.passwords");
        FileInputStream fileInputStream = null;
        try {
            final Properties passproperties = new Properties();
            fileInputStream = new FileInputStream(passfilename);
            passproperties.load(fileInputStream);
            usernames = Lists.newArrayList(csvSplitter.split(passproperties.getProperty("testbed.usernames")));
            passwords = Lists.newArrayList(csvSplitter.split(passproperties.getProperty("testbed.passwords")));

        } catch (final IOException e) {
            log.error(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (final IOException e) {
                    log.error(e);
                }
            }
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


        imagePath = properties.getProperty("image.telosb");
        reFlash(properties.getProperty("nodes.telosb").split(","));
    }

//    private void flashNodes(String[] nodes, String type) {
//        log.info("|==  Checking for " + type + " nodes");
//        log.info("|== Need to reFlash the " + type + " nodes.");
//        reFlash(nodes);
//    }

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
