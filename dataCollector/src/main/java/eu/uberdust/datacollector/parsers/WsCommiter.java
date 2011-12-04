package eu.uberdust.datacollector.parsers;

import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
import eu.uberdust.reading.LinkReading;
import eu.uberdust.reading.NodeReading;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/4/11
 * Time: 2:22 PM
 */
public class WsCommiter {
    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(WsCommiter.class);

    /**
     * Constructor using a NodeReading.
     *
     * @param nodeReading the NodeReading to commit
     */
    public WsCommiter(final NodeReading nodeReading) {
        try {
            LOGGER.info("adding " + nodeReading);
            InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading);
            LOGGER.info("added " + nodeReading);
        } catch (Exception e) {
            LOGGER.error("InsertReadingWebSocketClient -node-" + e);
        }
    }

    /**
     * Constructor using a LinkReading.
     *
     * @param linkReading the LinkReading to commit
     */
    public WsCommiter(final LinkReading linkReading) {
        try {
            LOGGER.info("adding " + linkReading);
            InsertReadingWebSocketClient.getInstance().setLinkReading(linkReading);
            LOGGER.info("added " + linkReading);
        } catch (Exception e) {
            LOGGER.error("InsertReadingWebSocketClient -link- " + e);
        }
    }
}
