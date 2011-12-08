package eu.uberdust.websockets.insert;

import com.caucho.websocket.AbstractWebSocketListener;
import com.caucho.websocket.WebSocketContext;
import eu.uberdust.uberlogger.UberLogger;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;

/**
 * Insert Reading Web Socket Listener.
 */
public final class InsertReadingWebSocketListener extends AbstractWebSocketListener {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocketListener.class);

    /**
     * Singleton instance.
     */
    private static InsertReadingWebSocketListener ourInstance = null;

    /**
     * Delimiter.
     */
    private static final String DELIMITER = "@";

    /**
     * NodeReading persistence manager.
     */
    private NodeReadingController nodeReadingManager;

    /**
     * LinkReading persistence manager.
     */
    private LinkReadingController linkReadingManager;


    /**
     * Constructor.
     */
    private InsertReadingWebSocketListener() {
        // empty constructor
    }

    /**
     * Returns singleton instance.
     *
     * @return singleton instance.
     */
    public static InsertReadingWebSocketListener getInstance() {
        synchronized (InsertReadingWebSocketListener.class) {

            if (ourInstance == null) {
                ourInstance = new InsertReadingWebSocketListener();
            }
            return ourInstance;
        }
    }

    /**
     * Sets node reading persistence manager.
     *
     * @param nodeReadingManager node reading persistence manager.
     */
    public void setNodeReadingManager(final NodeReadingController nodeReadingManager) {
        this.nodeReadingManager = nodeReadingManager;
    }

    /**
     * Sets link reading persistence manager.
     *
     * @param linkReadingManager link reading manager.
     */
    public void setLinkReadingManager(final LinkReadingController linkReadingManager) {
        this.linkReadingManager = linkReadingManager;
    }

    /**
     * On start of connection.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onStart(final WebSocketContext context) throws IOException {
        super.onStart(context);
        LOGGER.info("onStart()");
    }

    /**
     * On read binary.
     *
     * @param context WebSocketContext instance.
     * @param is      InputStream instance.
     * @throws IOException IOException exception.
     */
    public void onReadBinary(final WebSocketContext context, final InputStream is) throws IOException {
        final StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        final String receivedMessage = writer.toString();
        writer.close();
        LOGGER.info("onReadBinary(): " + receivedMessage);
        final String[] messageParts = receivedMessage.split(DELIMITER);
        final String classOfReading = messageParts[0];
        final int testbedId = Integer.parseInt(messageParts[1]);
        String message = "Neither Node nor Link reading. ERROR";

        if (receivedMessage.contains("1ccd")) {
            UberLogger.getInstance().LOG(Long.parseLong(messageParts[4]), "T23");
        }

        try {
            if (classOfReading.contains("NodeReading")) {
                // node reading incoming
                final String nodeId = messageParts[2];
                final String capabilityId = messageParts[3];
                final long timestamp = Long.parseLong(messageParts[4]);
                final double readingValue = Double.parseDouble(messageParts[5]);

                if (receivedMessage.contains("1ccd")) {
                    UberLogger.getInstance().LOG(Long.parseLong(messageParts[4]), "T24");
                }

                nodeReadingManager.insertReading(nodeId, capabilityId, testbedId, readingValue, new Date(timestamp));
                message = new StringBuilder().append("Inserted for Node(").append(nodeId).append(") Capability(").append(capabilityId).append(") Testbed(").append(testbedId).append(") : [").append(timestamp).append(",").append(readingValue).append("]. OK").toString();

            } else if (classOfReading.contains("LinkReading")) {
                // link reading incoming
                final String sourceNodeId = messageParts[2];
                final String targetNodeId = messageParts[3];
                final String capabilityId = messageParts[4];
                final long timestamp = Long.parseLong(messageParts[5]);
                final double readingValue = Double.parseDouble(messageParts[6]);
                linkReadingManager.insertReading(sourceNodeId, targetNodeId, capabilityId, testbedId, readingValue, 0.0, new Date(timestamp));
                message = new StringBuilder().append("Inserted for Link[").append(sourceNodeId).append(",").append(targetNodeId).append("] Capability(").append(capabilityId).append(") Testbed(").append(testbedId).append(") : [").append(timestamp).append(",").append(readingValue).append("]. OK").toString();
            }
        } catch (Exception e) {
            message = "Exception OCCURED. ERROR";
            LOGGER.error(e);
        } finally {
            LOGGER.info("Sending " + message);
            // After message is set return it to client.
            final PrintWriter pw = context.startTextMessage();
            pw.print(message);
            pw.close();
        }
        if (receivedMessage.contains("1ccd")) {
            UberLogger.getInstance().LOG(Long.parseLong(messageParts[4]), "T25");
        }

        LOGGER.info("MEMSTAT_1: " + Runtime.getRuntime().totalMemory() + ":" + Runtime.getRuntime().freeMemory() + " -- " + Runtime.getRuntime().freeMemory() * 100 / Runtime.getRuntime().totalMemory() + "% free mem");
        /*Runtime.getRuntime().gc();
        LOGGER.info("MEMSTAT_2: " + Runtime.getRuntime().totalMemory() + ":" + Runtime.getRuntime().freeMemory() + " -- " + Runtime.getRuntime().freeMemory() * 100 / Runtime.getRuntime().totalMemory() + "% free mem");*/

    }

    /**
     * On read text.
     *
     * @param context WebSocketContext instance.
     * @param is      InputStream instance.
     * @throws IOException IOException exception.
     */
    public void onReadText(final WebSocketContext context, final Reader is) throws IOException {
        LOGGER.info("onReadText()");
        super.onReadText(context, is);
    }

    /**
     * On close.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onClose(final WebSocketContext context) throws IOException {
        LOGGER.info("onClose()");
    }

    /**
     * On disconnect.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onDisconnect(final WebSocketContext context) throws IOException {
        super.onDisconnect(context);
        LOGGER.info("onDisconnect()");

    }

    /**
     * On timeout.
     *
     * @param context WebSocketContext instance.
     * @throws IOException IOException exception.
     */
    public void onTimeout(final WebSocketContext context) throws IOException {
        LOGGER.info("onTimeout()");
    }
}
