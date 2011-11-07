package eu.uberdust.websockets;

import com.caucho.websocket.AbstractWebSocketListener;
import com.caucho.websocket.WebSocketContext;
import eu.wisebed.wisedb.listeners.LastNodeReadingObservable;
import eu.wisebed.wisedb.model.NodeReading;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/8/11
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomWebSocketListener extends AbstractWebSocketListener implements Observer {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomWebSocketListener.class);

    /**
     * A List with the connected users.
     */
    private final List<WebSocketContext> users = new ArrayList<WebSocketContext>();

    /**
     * The protocol which been handled from the specific listener.
     */
    private final String thisProtocol;

    /**
     * Constructor.
     *
     * @param protocol protocol ID.
     */
    public CustomWebSocketListener(final String protocol) {
        super();
        thisProtocol = protocol;
    }

    @Override
    public void onStart(final WebSocketContext context) throws IOException {
        super.onStart(context);
        users.add(context);
        LOGGER.info("onStart");
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
    }

    @Override
    public void onDisconnect(final WebSocketContext context) throws IOException {
        super.onDisconnect(context);
        LOGGER.info("onDisconnect");
        users.remove(context);
    }

    @Override
    public void onTimeout(final WebSocketContext context) throws IOException {
        super.onTimeout(context);
        LOGGER.info("onTimeout");
        users.remove(context);
    }


    @Override
    public void update(final Observable o, final Object arg) {
        if (o instanceof LastNodeReadingObservable && arg instanceof NodeReading) {
            final NodeReading lastReading = (NodeReading) arg;
            LOGGER.info(new StringBuilder().append(lastReading.getTimestamp()).append(":").append(lastReading.getReading()).toString());
            LOGGER.info(this);
            if ((new StringBuilder().append(lastReading.getCapability().getName()).append(":").append(lastReading.getNode().getId()).toString())
                    .equals(thisProtocol)) {

                final String response = new StringBuilder().append(lastReading.getTimestamp()).append(":").append(lastReading.getReading()).toString();
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
        }
    }
}
