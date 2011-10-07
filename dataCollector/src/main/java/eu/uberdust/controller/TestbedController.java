package eu.uberdust.controller;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import de.uniluebeck.itm.wisebed.cmdlineclient.protobuf.ProtobufControllerClient;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.WSNAsyncWrapper;
import eu.uberdust.controller.communication.SocketServer;
import eu.uberdust.controller.protobuf.CommandProtocol;
import eu.wisebed.api.common.Message;
import eu.wisebed.api.sm.ExperimentNotRunningException_Exception;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.api.sm.UnknownReservationIdException_Exception;
import eu.wisebed.api.wsn.WSN;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
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

        //Start the Socket Server
        (new SocketServer()).start();
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
        //pcc.addListener(new ControllerClientListener());

    }

    public void sendCommand(CommandProtocol.Command.Builder protoCommand) {

        // Send a message to nodes via uart (to receive them enable RX_UART_MSGS in the fronts_config.h-file)
        final Message msg = new Message();

        final String macAddress = protoCommand.getDestination().substring(protoCommand.getDestination().indexOf("0x") + 2);
        final byte[] macBytes = new byte[2];
        if (macAddress.length() == 4) {
            macBytes[0] = Integer.valueOf(macAddress.substring(0, 2), 16).byteValue();
            macBytes[1] = Integer.valueOf(macAddress.substring(2, 4), 16).byteValue();
        } else if (macAddress.length() == 3) {
            macBytes[0] = Integer.valueOf(macAddress.substring(0, 1), 16).byteValue();
            macBytes[1] = Integer.valueOf(macAddress.substring(1, 3), 16).byteValue();
        }

        final String[] strPayload = protoCommand.getPayload().split(",");
        final byte[] payload = new byte[strPayload.length];
        for (int i =0 ; i< payload.length;i++){
              payload[i] = Integer.valueOf(strPayload[i],16).byteValue();
        }

        final byte[] newPayload = new byte[macBytes.length + payload.length + 1 + PAYLOAD_HEADERS.length];
        newPayload[0] = PAYLOAD_PREFIX;
        System.arraycopy(macBytes, 0, newPayload, 1, macBytes.length);
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
