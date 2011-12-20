package eu.uberdust.exampleapps;

import eu.uberdust.communication.rest.InsertReadingRestClient;
import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
import eu.uberdust.reading.LinkReading;
import eu.uberdust.reading.NodeReading;
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

        // sample node readings
        NodeReading nodeReading1 = new NodeReading();
        nodeReading1.setTestbedId("1");
        nodeReading1.setNodeId("urn:ctinetwork:carrot_delete_me");
        nodeReading1.setCapabilityName("urn:ctinetwork:node:capability:lockScreen");
        nodeReading1.setTimestamp(Long.toString(new Date().getTime()));
        nodeReading1.setReading("30.2");
        LOGGER.info(nodeReading1.toDelimitedString());

        NodeReading nodeReading2 = new NodeReading();
        nodeReading2.setTestbedId("1");
        nodeReading2.setNodeId("urn:ctinetwork:carrot_delete_moi");
        nodeReading2.setCapabilityName("urn:ctinetwork:node:capability:lockScreen");
        nodeReading2.setTimestamp(Long.toString(new Date().getTime()));
        nodeReading2.setReading("30.0");
        LOGGER.info(nodeReading2.toDelimitedString());

        // sample link readings
        LinkReading linkReading1 = new LinkReading();
        linkReading1.setTestbedId("1");
        linkReading1.setLinkSource("urn:ctinetwork:source");
        linkReading1.setLinkTarget("urn:ctinetwork:target");
        linkReading1.setCapabilityName("command1");
        linkReading1.setTimestamp(Long.toString(new Date().getTime()));

        /**
         * REST Call Nodes #1
         */

        final String restBaseUrl = "http://carrot.cti.gr:8080/uberdust/rest";

        // insert node reading using REST
        LOGGER.info("Calling REST at (" + restBaseUrl + nodeReading1.toRestString() + ")");
        String result =
                InsertReadingRestClient.getInstance().callRestfulWebService(restBaseUrl + nodeReading1.toRestString());
        if (!result.contains("OK")) {
            LOGGER.error("Could not insert reading");
            throw new RuntimeException("Could not insert reading");
        }
        LOGGER.info("Calling REST at (" + restBaseUrl + nodeReading2.toRestString() + ")");
        result =
                InsertReadingRestClient.getInstance().callRestfulWebService(restBaseUrl + nodeReading2.toRestString());
        if (!result.contains("OK")) {
            LOGGER.error("Could not insert reading");
            throw new RuntimeException("Could not insert reading");
        }

        /**
         * WebSocket Call Nodes #2
         */

        final String webSocketUrl = "ws://carrot.cti.gr:8080/uberdust/insertreading.ws";

        // insert node reading using WebSockets
        LOGGER.info("Calling WebSocket at (" + webSocketUrl + ") connecting");
        InsertReadingWebSocketClient.getInstance().connect(webSocketUrl);
        LOGGER.info("Calling sendNodeReading(nodeReading1) (" + nodeReading1.toDelimitedString() + ")");
        InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading1);
        LOGGER.info("Calling sendNodeReading(nodeReading2) (" + nodeReading2.toDelimitedString() + ")");
        InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading2);
        InsertReadingWebSocketClient.getInstance().disconnect();
        LOGGER.info("Disconnect");

        /**
         * REST Call Links #1
         */

//        // insert node reading using REST
//        LOGGER.info("Calling REST at (" + restBaseUrl + nodeReading1.toRestString() + ")");
//        result =
//                InsertReadingRestClient.getInstance().callRestfulWebService(restBaseUrl + nodeReading1.toRestString());
//        if (!result.contains("OK")) {
//            LOGGER.error("Could not insert reading");
//            throw new RuntimeException("Could not insert reading");
//        }
//        LOGGER.info("Calling REST at (" + restBaseUrl + nodeReading2.toRestString() + ")");
//        result =
//                InsertReadingRestClient.getInstance().callRestfulWebService(restBaseUrl + nodeReading2.toRestString());
//        if (!result.contains("OK")) {
//            LOGGER.error("Could not insert reading");
//            throw new RuntimeException("Could not insert reading");
//        }

        /**
         * WebSocket Call Links #2
         */

        // insert node reading using WebSockets
        LOGGER.info("Calling WebSocket at (" + webSocketUrl + ") connecting");
        InsertReadingWebSocketClient.getInstance().connect(webSocketUrl);
        LOGGER.info("Calling sendNodeReading(linkReading1)");
        InsertReadingWebSocketClient.getInstance().sendLinkReading(linkReading1);
        InsertReadingWebSocketClient.getInstance().disconnect();
        LOGGER.info("Disconnect");
    }
}
