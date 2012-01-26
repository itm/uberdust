package eu.uberdust;

import eu.uberdust.communication.RestClient;
import org.apache.log4j.Logger;

/**
 * Evaluation Application Main Class.
 */
public class MainApp {

    /**
     * Evaluation String.
     */
    private static final String EVALUATION_NODE = "urn:wisebed:ctitestbed:0xa4a";

    /**
     * Evaluation payload.
     */
    private static final String EVALUATION_PAYLOAD = "99,1";

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MainApp.class);

    /**
     * Main routine of application.
     * @param args input arguments
     */
    public static void main(final String[] args) {
                LOGGER.info("Evaluation Application.");
                RestClient.getInstance().callRestfulWebService(
                "http://uberdust.cti.gr/rest/sendCommand/destination/" + EVALUATION_NODE + "/payload/"
                        + EVALUATION_PAYLOAD);
    }
}
