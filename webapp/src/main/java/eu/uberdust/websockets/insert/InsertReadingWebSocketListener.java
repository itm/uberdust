package eu.uberdust.websockets.insert;

import com.caucho.websocket.AbstractWebSocketListener;
import com.caucho.websocket.WebSocketContext;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import eu.wisebed.wisedb.exception.UnknownTestbedException;
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
     * Delimiter.
     */
    private static final String DELIMITER = "@";

    /**
     * NodeReading peristence manager.
     */
    private transient NodeReadingController nodeReadingManager;

    /**
     * LinkReading peristence manager.
     */
    private transient LinkReadingController linkReadingManager;

    /**
     * Constructor.
     */
    public InsertReadingWebSocketListener() {
        //empty constructor.
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
     * @param linkReadingManager link reading persistence manager.
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
        LOGGER.info("onReadBinary()");
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        final String receivedMessage = writer.toString();
        LOGGER.info("onReadBinary(): " + receivedMessage);
        String[] messageParts = receivedMessage.split(DELIMITER);
        final String classOfReading = messageParts[0];

        LOGGER.info(classOfReading);
        if (classOfReading.contains("NodeReading")) {
            // node reading incoming
           LOGGER.info("NodeReading");
            final int testbedId = Integer.parseInt(messageParts[1]);
            final String nodeId = messageParts[2];
            final String capabilityId = messageParts[3];
            final long timestamp = Long.parseLong(messageParts[4]);
            final double readingValue = Double.parseDouble(messageParts[5]);

            try {
                nodeReadingManager.insertReading(nodeId, capabilityId, testbedId, readingValue, new Date(timestamp));
                PrintWriter printWriter = context.startTextMessage();
                printWriter.write("OK");
                printWriter.close();
            } catch (UnknownTestbedException e) {
                PrintWriter printWriter = context.startTextMessage();
                printWriter.write("Unknown Testbed Exception . Closing WebSocket");
                printWriter.close();
                context.close();
            }
        } else if (classOfReading.contains("LinkReading")) {
            // link reading incoming
            LOGGER.info("LinkReading");
            final int testbedId = Integer.parseInt(messageParts[1]);
            final String sourceNodeId = messageParts[2];
            final String targetNodeId = messageParts[3];
            final String capabilityId = messageParts[4];
            final long timestamp = Long.parseLong(messageParts[5]);
            final double readingValue = Double.parseDouble(messageParts[6]);

            try {
                linkReadingManager.insertReading(sourceNodeId, targetNodeId, capabilityId, testbedId, readingValue, 0.0, new Date(timestamp));
                PrintWriter printWriter = context.startTextMessage();
                printWriter.write("OK");
                printWriter.close();
            } catch (UnknownTestbedException e) {
                PrintWriter printWriter = context.startTextMessage();
                printWriter.write("Unknown Testbed Exception . Closing WebSocket");
                printWriter.close();
                context.close();
            }

        } else {
            // unknown stuff incoming
             LOGGER.info("UNKNOWN");
            PrintWriter printWriter = context.startTextMessage();
            printWriter.write("Neither Node nor link reading. Closing WebSocket");
            printWriter.close();
            context.close();
        }
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
