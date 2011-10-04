package eu.uberdust.controller;

import com.google.common.collect.Lists;
import de.itm.uniluebeck.tr.wiseml.WiseMLHelper;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClient;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.WSNAsyncWrapper;
import eu.uberdust.controller.util.ControllerClientListener;
import eu.wisebed.api.common.Message;
import eu.wisebed.api.sm.ExperimentNotRunningException_Exception;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.api.sm.UnknownReservationIdException_Exception;
import eu.wisebed.api.wsn.WSN;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.omg.SendingContext.RunTime;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/3/11
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestbedController {

    private final static Logger LOGGER = Logger.getLogger(TestbedController.class);
    private final static byte PAYLOAD_PREFIX = 0xb;
    private final static byte[] PAYLOAD_HEADERS = new byte[]{0x7f, 0x69, 0x70};

    private String secretReservationKeys;
    private String sessionManagementEndpointURL;
    private String nodeUrnsToListen;
    private String pccHost;
    private Integer pccPort;


    private WSNAsyncWrapper wsn;
    private List<String> nodeURNs = new ArrayList<String>();


    /**
     * static instance(ourInstance) initialized as null.
     */
    private static TestbedController ourInstance = null;

    /**
     * TestbedController is loaded on the first execution of TestbedController.getInstance()
     * or the first access to TestbedController.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static TestbedController getInstance() {
        synchronized (TestbedController.class) {
            if (ourInstance == null) {
                ourInstance = new TestbedController();
            }
        }

        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private TestbedController() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        readProperties();
        connectToRuntime();

    }

    private void readProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("testbedController.properties"));
        } catch (final IOException e) {
            throw new RuntimeException("No properties file found! dataCollector.properties not found!");
        }

        secretReservationKeys = properties.getProperty("testbed.secretReservationKeys");
        sessionManagementEndpointURL = properties.getProperty("testbed.sessionManagementEndpointURL");
        nodeUrnsToListen = properties.getProperty("testbed.nodeUrns");
        pccHost = properties.getProperty("testbed.session");
        pccPort = Integer.parseInt(properties.getProperty("testbed.port"));
        nodeURNs = Lists.newArrayList(nodeUrnsToListen.split(","));

        if (nodeURNs.isEmpty()) {
            throw new RuntimeException("No node Urns To Listen");
        } else {
            for (String nodeURN : nodeURNs) {
                LOGGER.info(nodeURN);
            }
        }
    }

    private void connectToRuntime() {

        String wsnEndpointURL = null;
        final SessionManagement sessionManagement = WSNServiceHelper.getSessionManagementService(sessionManagementEndpointURL);
        try {
            wsnEndpointURL = sessionManagement.getInstance(BeanShellHelper.parseSecretReservationKeys(secretReservationKeys), "NONE");
        } catch (final ExperimentNotRunningException_Exception e) {
            LOGGER.error(e);
        } catch (final UnknownReservationIdException_Exception e) {
            LOGGER.error(e);
        }
        LOGGER.info("Got a WSN instance URL, endpoint is: " + wsnEndpointURL);

        final WSN wsnService = WSNServiceHelper.getWSNService(wsnEndpointURL);
        wsn = WSNAsyncWrapper.of(wsnService);
        LOGGER.info("Retrieved the following node URNs: {}" + nodeURNs);

        final ProtobufControllerClient pcc = ProtobufControllerClient.create(pccHost, pccPort, BeanShellHelper.parseSecretReservationKeys(secretReservationKeys));
        pcc.connect();
        pcc.addListener(new ControllerClientListener());

    }

    public void sendCommand(final byte[] destination, final byte[] payload) {

        // Send a message to nodes via uart (to receive them enable RX_UART_MSGS in the fronts_config.h-file)
        final Message msg = new Message();

        final byte[] newPayload = new byte[destination.length + payload.length + 1 + PAYLOAD_HEADERS.length];

        newPayload[0] = PAYLOAD_PREFIX;
        System.arraycopy(destination, 0, newPayload, 1, destination.length);
        System.arraycopy(PAYLOAD_HEADERS, 0, newPayload, 3, PAYLOAD_HEADERS.length);
        System.arraycopy(payload, 0, newPayload, 6, payload.length);

        msg.setBinaryData(newPayload);
        msg.setSourceNodeId("urn:wisebed:ctitestbed:0x1");

        LOGGER.info("Sending message - " + Arrays.toString(newPayload));
        try {
            msg.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) GregorianCalendar.getInstance()));
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error(e);
        }
        wsn.send(nodeURNs, msg, 10, TimeUnit.SECONDS);
    }


    public static void main(String[] args) {
        TestbedController.getInstance();
    }

}
