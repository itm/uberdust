package eu.uberdust.exampleapps;

import eu.uberdust.communication.rest.InsertReadingRestClient;
import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
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

        // sample node reading
        NodeReading nodeReading1 = new NodeReading();
        nodeReading1.setTestbedId("3");
        nodeReading1.setNodeId("urn:ctinetwork:carrot_delete_me");
        nodeReading1.setCapabilityName("urn:ctinetwork:node:capability:lockScreen");
        nodeReading1.setTimestamp(Long.toString(new Date().getTime()));
        nodeReading1.setReading("1.0");
        LOGGER.info(nodeReading1.toDelimitedString());

        NodeReading nodeReading2 = new NodeReading();
        nodeReading2.setTestbedId("3");
        nodeReading2.setNodeId("urn:ctinetwork:carrot_delete_moi");
        nodeReading2.setCapabilityName("urn:ctinetwork:node:capability:lockScreen");
        nodeReading2.setTimestamp(Long.toString(new Date().getTime()));
        nodeReading2.setReading("1.0");
        LOGGER.info(nodeReading2.toDelimitedString());


        /**
         * REST Call
         */

        final String restBaseUrl = "http://uberdust.cti.gr/rest";

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
         * WebSocket Call
         */

        final String webSocketUrl = "ws://carrot.cti.gr:8080/uberdust/insertreading.ws";

        // insert node reading using WebSockets
        LOGGER.info("Calling WebSocket at (" + webSocketUrl + ") connecting");
        InsertReadingWebSocketClient.getInstance().connect(webSocketUrl);
        LOGGER.info("Calling sendNodeReading(nodeReading1)");
        InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading1);
        LOGGER.info("Calling sendNodeReading(nodeReading2)");
        InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading2);
    }
}
