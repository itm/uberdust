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
     * Double Reading type.
     */
    private static final String DOUBLE_READING = "D";

    /**
     * String Reading type.
     */
    private static final String STRING_READING = "S";

    /**
     * Both reading type.
     */
    private static final String BOTH_READING = "B";


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
            UberLogger.getInstance().log(Long.parseLong(messageParts[4]), "T23");
        }

        try {
            if (classOfReading.contains("NodeReading")) {
                // node reading incoming
                final String nodeId = messageParts[2];
                final String capabilityId = messageParts[3];
                final long timestamp = Long.parseLong(messageParts[4]);
                final String readingType = messageParts[5];
                Double readingValue = null;
                String stringReadingValue = null;
                if (readingType.equals(DOUBLE_READING)) {
                    readingValue = Double.parseDouble(messageParts[6]);
                } else if (readingType.equals(STRING_READING)) {
                    stringReadingValue = messageParts[6];
                } else if (readingType.equals(BOTH_READING)) {
                    readingValue =  Double.parseDouble(messageParts[6]);
                    stringReadingValue = messageParts[7];
                }

                if (receivedMessage.contains("1ccd")) {
                    UberLogger.getInstance().log(Long.parseLong(messageParts[4]), "T24");
                }

                nodeReadingManager.insertReading(nodeId, capabilityId, testbedId, readingValue , stringReadingValue ,
                        new Date(timestamp));
                message = new StringBuilder().append("Inserted for Node(").append(nodeId).append(") Capability(")
                        .append(capabilityId).append(") Testbed(").append(testbedId).append(") : [")
                        .append(timestamp).append(",").append(readingValue).append("]. OK").toString();

            } else if (classOfReading.contains("LinkReading")) {
                // link reading incoming
                final String sourceNodeId = messageParts[2];
                final String targetNodeId = messageParts[3];
                final String capabilityId = messageParts[4];
                final long timestamp = Long.parseLong(messageParts[5]);
                final String readingType = messageParts[6];
                Double readingValue = null;
                String stringReadingValue = null;
                if (readingType.equals(DOUBLE_READING)) {
                    readingValue = Double.parseDouble(messageParts[7]);
                } else if (readingType.equals(STRING_READING)) {
                    stringReadingValue = messageParts[7];
                } else if (readingType.equals(BOTH_READING)) {
                    readingValue =  Double.parseDouble(messageParts[7]);
                    stringReadingValue = messageParts[8];
                }
                linkReadingManager.insertReading(sourceNodeId, targetNodeId, capabilityId, testbedId, readingValue,
                        stringReadingValue, null, new Date(timestamp));
                message = new StringBuilder().append("Inserted for Link[").append(sourceNodeId).append(",")
                        .append(targetNodeId).append("] Capability(").append(capabilityId).append(") Testbed(")
                        .append(testbedId).append(") : [").append(timestamp).append(",").append(readingValue)
                        .append("]. OK").toString();
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
            UberLogger.getInstance().log(Long.parseLong(messageParts[4]), "T25");
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
        char[] arr = new char[1024]; // 1K at a time
        StringBuffer buf = new StringBuffer();
        int numChars;

        while ((numChars = is.read(arr, 0, arr.length)) > 0) {
            buf.append(arr, 0, numChars);
        }
        LOGGER.info("onReadText() : " + buf.toString());
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
