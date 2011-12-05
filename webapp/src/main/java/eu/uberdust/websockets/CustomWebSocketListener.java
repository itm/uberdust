package eu.uberdust.websockets;

import com.caucho.websocket.AbstractWebSocketListener;
import com.caucho.websocket.WebSocketContext;
import eu.uberdust.uberlogger.UberLogger;
import eu.wisebed.wisedb.listeners.AbstractNodeReadingListener;
import eu.wisebed.wisedb.model.NodeReading;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/8/11
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomWebSocketListener extends AbstractWebSocketListener implements AbstractNodeReadingListener {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomWebSocketListener.class);

    /**
     * A List with the connected users.
     */
    private final List<WebSocketContext> users = new ArrayList<WebSocketContext>();

    /**
     * The Node id..
     */
    private final String nodeID;

    /**
     * The capaility id.
     */
    private final String capabilityID;

    /**
     * Constructor.
     *
     * @param nodeID       the node ID.
     * @param capabilityID the capability ID.
     */
    public CustomWebSocketListener(final String nodeID, final String capabilityID) {
        super();
        this.nodeID = nodeID;
        this.capabilityID = capabilityID;
        String thisProtocol = new StringBuilder().append(nodeID).append(":").append(capabilityID).toString();

    }

    @Override
    public void onStart(final WebSocketContext context) throws IOException {
        super.onStart(context);
        LOGGER.info("onStart");
        users.add(context);
        LOGGER.info(users.size());
        context.setTimeout(-1);
    }

    @Override
    public void onReadBinary(final WebSocketContext context, final InputStream is) throws IOException {
        super.onReadBinary(context, is);
    }

    @Override
    public void onReadText(final WebSocketContext context, final Reader is) throws IOException {
        super.onReadText(context, is);
    }

    @Override
    public void onClose(final WebSocketContext context) throws IOException {
        super.onClose(context);
        LOGGER.info("onClose");
        users.remove(context);
        LOGGER.info(users.size());
        /*if (users.size() == 0) {
            LastNodeReadingConsumer.getInstance().removeListener(nodeID, capabilityID);
        }*/
    }

    @Override
    public void onDisconnect(final WebSocketContext context) throws IOException {
        super.onDisconnect(context);
        LOGGER.info("onDisconnect");
        users.remove(context);
        LOGGER.info(users.size());
        /*   if (users.size() == 0) {
            LastNodeReadingConsumer.getInstance().removeListener(nodeID, capabilityID);
        }*/

    }

    @Override
    public void onTimeout(final WebSocketContext context) throws IOException {
        super.onTimeout(context);
        LOGGER.info("onTimeout");
    }

    @Override
    public void update(final NodeReading lastReading) {
        LOGGER.info("Update");
        if (lastReading.getNode().getId().contains("1ccd")) {
            UberLogger.getInstance().LOG(lastReading.getTimestamp().getTime(), "T51");
        }
        if (lastReading.getNode().getId().equals(nodeID) && lastReading.getCapability().getName().equals(capabilityID)) {
            final String response = new StringBuilder().append(lastReading.getTimestamp().getTime()).append("\t").append(lastReading.getReading()).toString();
            LOGGER.info(response);
            for (final WebSocketContext user : users) {
                try {
                    final PrintWriter thisWriter = user.startTextMessage();
                    thisWriter.println(response);
                    thisWriter.close();
                } catch (final IOException e) {
                    LOGGER.error(e);
                }
            }
        }
        if (lastReading.getNode().getId().contains("1ccd")) {
            UberLogger.getInstance().LOG(lastReading.getTimestamp().getTime(), "T52");
        }
    }
}
