package eu.uberdust;

import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
import eu.uberdust.eu.uberdust.reading.NodeReading;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Date;

/**
 * The Main Class of the App.
 */
public final class InsertReadingMainApp {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingMainApp.class);

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private InsertReadingMainApp() {
        // empty constructor
    }

    /**
     * Main method.
     * @param args input arguments.
     * @throws Exception exception.
     */
    public static void main(final String[] args) throws Exception {

        /**
         * Setting up
         */

        // log4j properties
        PropertyConfigurator.configure(InsertReadingMainApp.class.getClassLoader().getResource("log4j.properties"));

        // sample node reading
        NodeReading nodeReading = new NodeReading();
        nodeReading.setTestbedId("3");
        nodeReading.setNodeId("urn:ctinetwork:carrot_delete_me");
        nodeReading.setCapabilityName("urn:ctinetwork:node:capability:lockScreen");
        nodeReading.setTimestamp(Long.toString(new Date().getTime()));
        nodeReading.setReading("1.0");
        LOGGER.info(nodeReading.toString());

//
//        /**
//         * REST Call
//         */
//
//        final String restBaseUrl = "http://uberdust.cti.gr/rest";
//
//        // insert node reading using REST
//        LOGGER.info("Calling REST at (" + restBaseUrl + nodeReading.toRestString() + ")");
//        final String result =
//                InsertReadingRestClient.getInstance().callRestfulWebService(restBaseUrl + nodeReading.toRestString());
//
//        if (!result.contains("OK")) {
//            LOGGER.error("Could not insert reading");
//            throw new RuntimeException("Could not insert reading");
//        }

        /**
         * WebSocket Call
         */

        final String webSocketUrl = "ws://carrot.cti.gr:8080/uberdust/insertreading.ws";

        // insert node reading using WebSockets
        LOGGER.info("Calling WebSocket at (" + webSocketUrl + ") connecting");
        InsertReadingWebSocketClient.getInstance().connect(webSocketUrl);
        LOGGER.info("Calling sendNodeReading()");
        InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading);

    }
}
