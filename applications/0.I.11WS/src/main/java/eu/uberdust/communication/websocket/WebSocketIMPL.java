package eu.uberdust.communication.websocket;

import eu.uberdust.lights.LightController;
import eu.uberdust.uberlogger.UberLogger;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;

import java.util.Date;

/**
 * The Implementation of the WebSocket Interface.
 */
public class WebSocketIMPL implements WebSocket.OnTextMessage {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(WebSocketIMPL.class);

    /**
     * Called with a complete text message when all fragments have been received.
     * The maximum size of text message that may be aggregated from multiple
     * frames is set with {@link Connection#setMaxTextMessageSize(int)}.
     *
     * @param data The message
     */
    @Override
    public final void onMessage(final String data) {
        if (data.isEmpty()) {
            return;
        }
        UberLogger.getInstance().log(Long.valueOf(data.split("\t")[0]), "T6");
        //System.out.println(Long.valueOf(data.split("\t")[0]));
        LOGGER.info(new StringBuilder().append("-- onMessage: ").append(data).append(new Date()).toString());
        //LightController.getInstance().setLastReading(System.currentTimeMillis());
        LightController.getInstance().setLastReading(Long.valueOf(data.split("\t")[0]));
        LOGGER.info(new StringBuilder().append("-- finished : ").append(new Date()).toString());
    }

    /**
     * Called when a new websocket connection is accepted.
     *
     * @param connection The Connection object to use to send messages.
     */
    @Override
    public final void onOpen(final Connection connection) {
        LOGGER.info("onOpen");
    }

    /**
     * Called when an established websocket connection closes.
     *
     * @param closeCode the Close Code
     * @param message   the Message
     */
    @Override
    public final void onClose(final int closeCode, final String message) {
        LOGGER.info("onClose");
        WSocketClient.getInstance().disconnect();
        WSocketClient.getInstance().restPing();
        WSocketClient.getInstance().connect();
    }
}
