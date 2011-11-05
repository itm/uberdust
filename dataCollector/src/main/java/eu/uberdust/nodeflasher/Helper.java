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
import org.apache.log4j.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Helper {

    private static final Logger LOGGER = Logger.getLogger(Helper.class);
    Properties properties;

    private RS reservationSystem;
    private SessionManagement sessionManagement;
    private ArrayList<String> usernames;
    private List urnPrefixes;
    private List secretAuthKeys;
    private String pccHost;
    private int pccPort;

    Helper() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("nodeFlasher.properties"));
        } catch (Exception e) {
            LOGGER.error("|*** No properties file found! nodeFlasher.properties not found!");
            return;
        }

    }

    void authenticate() {

        // Authentication credentials and other relevant information used again and again as method parameters

        final Splitter csvSplitter = Splitter.on(",").trimResults().omitEmptyStrings();

        urnPrefixes = Lists.newArrayList(csvSplitter.split(properties.getProperty("testbed.urnprefixes")));

        final String passfilename = properties.getProperty("testbed.passwords");
        FileInputStream fileInputStream = null;

        ArrayList<String> passwords = null;
        try {
            final Properties passproperties = new Properties();
            fileInputStream = new FileInputStream(passfilename);
            passproperties.load(fileInputStream);
            usernames = Lists.newArrayList(csvSplitter.split(passproperties.getProperty("testbed.usernames")));
            passwords = Lists.newArrayList(csvSplitter.split(passproperties.getProperty("testbed.passwords")));

        } catch (final IOException e) {
            LOGGER.error(e);

        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (final IOException e) {
                    LOGGER.error(e);
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
        final String snaaEndpointURL = properties.getProperty("testbed.snaa.endpointurl");
        final String rsEndpointURL = properties.getProperty("testbed.rs.endpointurl");
        final String smEndpointURL = properties.getProperty("testbed.sm.endpointurl");


        // Retrieve Java proxies of the endpoint URLs above
        SNAA authSystem = SNAAServiceHelper.getSNAAService(snaaEndpointURL);
        reservationSystem = RSServiceHelper.getRSService(rsEndpointURL);
        sessionManagement = WSNServiceHelper.getSessionManagementService(smEndpointURL);


        // build argument types
        final List credentialsList = new ArrayList();
        for (int i = 0; i < urnPrefixes.size(); i++) {

            final AuthenticationTriple credentials = new AuthenticationTriple();

            credentials.setUrnPrefix((String) urnPrefixes.get(i));
            credentials.setUsername(usernames.get(i));
            credentials.setPassword(passwords.get(i));

            credentialsList.add(credentials);
        }

        // do the authentication
        LOGGER.info("|   Authenticating to SNAA...");
        try {
            secretAuthKeys = authSystem.authenticate(credentialsList);
        } catch (AuthenticationExceptionException e) {
            LOGGER.error(e);
        } catch (SNAAExceptionException e) {
            LOGGER.error(e);
        }
        LOGGER.info("|   Successfully authenticated!");


    }


    String reserveNodes(final String[] nodes) {

        LOGGER.info("|+   Trying to reserve " + nodes.length + " nodes");

        final List nodeURNsToReserve = Lists.newArrayList(nodes);
        // create reservation request data to reserve all iSense nodes for 10 minutes
        final ConfidentialReservationData reservationData = BeanShellHelper.generateConfidentialReservationData(
                nodeURNsToReserve, new Date(System.currentTimeMillis()), 4, TimeUnit.MINUTES,
                urnPrefixes, usernames);

        List secretResKeys = null;
        try {
            secretResKeys = reservationSystem.makeReservation(
                    BeanShellHelper.copySnaaToRs(secretAuthKeys),
                    reservationData
            );
        } catch (AuthorizationExceptionException e) {
            LOGGER.error(e);
        } catch (RSExceptionException e) {
            LOGGER.error(e);
        } catch (ReservervationConflictExceptionException e) {
            LOGGER.error(e);
        }
        LOGGER.info("|+   Successfully reserved " + nodeURNsToReserve.size() + " nodes");
        LOGGER.info("|+   Reservation Key(s): " + BeanShellHelper.toString(secretResKeys));

        return BeanShellHelper.toString(secretResKeys);

    }

    List getReservations(XMLGregorianCalendar timeFrom, XMLGregorianCalendar timeTo) throws RSExceptionException {
        return reservationSystem.getReservations(timeFrom, timeTo);
    }

    void flash(final String[] nodes, final String type) {
        final String imagePath = properties.getProperty("image." + type);
        try {
            final String reskey = reserveNodes(nodes);
            String wsnEndpointURL = sessionManagement.getInstance(BeanShellHelper.parseSecretReservationKeys(reskey), "NONE");

            LOGGER.info("|+   Got a WSN instance URL, endpoint is: " + wsnEndpointURL);
            final WSN wsnService = WSNServiceHelper.getWSNService(wsnEndpointURL);
            final WSNAsyncWrapper wsn = WSNAsyncWrapper.of(wsnService);

            final ProtobufControllerClient pcc = ProtobufControllerClient.create(pccHost, pccPort, BeanShellHelper.parseSecretReservationKeys(reskey));
            pcc.addListener(new ProtobufControllerClientListener() {
                public void receive(final List msg) {
                    // nothing to do
                }

                public void receiveStatus(final List requestStatuses) {
                    wsn.receive(requestStatuses);
                }

                public void receiveNotification(List msgs) {
                    for (Object msg : msgs) {
                        LOGGER.info(msg);
                    }
                }

                public void experimentEnded() {
                    LOGGER.debug("|+   Experiment ended");
                    System.exit(0);
                }

                public void onConnectionEstablished() {
                    LOGGER.debug("|+   Connection established.");
                }

                public void onConnectionClosed() {
                    LOGGER.debug("|+   Connection closed.");
                }
            });
            pcc.connect();

            // retrieve reserved node URNs from testbed
            final List nodeURNsToFlash = Lists.newArrayList(nodes);

            LOGGER.info("|+   Flashing nodes...");


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


            final Future flashFuture = wsn.flashPrograms(nodeURNsToFlash, programIndices, programs, 3, TimeUnit.MINUTES);
            JobResult flashJobResult;

            flashJobResult = (JobResult) flashFuture.get();

            LOGGER.info(flashJobResult);
            if (flashJobResult.getSuccessPercent() < 100) {
                LOGGER.info("|*   Not all nodes could be flashed. Exiting");
            }


            LOGGER.info("|+   Closing connection...");
            pcc.disconnect();

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public String[] getNodes(String s) {
        return properties.getProperty(s).split(",");
    }
}
