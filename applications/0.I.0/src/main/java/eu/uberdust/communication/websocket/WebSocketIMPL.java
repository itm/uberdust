package eu.uberdust.communication.websocket;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;

/**
 * The Implementation of the WebSocket Interface.
 */
public class WebSocketIMPL implements WebSocket.OnTextMessage {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(WebSocketIMPL.class);

    private String PROTOCOL;

    /**
     * Default Constructor.
     *
     * @param protocol the protocol.
     */
    public WebSocketIMPL(final String protocol) {
        PROTOCOL = protocol;
    }

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
        LOGGER.info(data.split("\t")[1]);

        if (PROTOCOL.equals(WSocketClient.PROTOCOL_LIGHT_OUT)) {
            final Double value = Double.parseDouble(data.split("\t")[1]);
            LOGGER.info("Lum: " + value);

            LightController.getInstance().setLastReading(value);

        } else if (PROTOCOL.equals(WSocketClient.PROTOCOL_LOCK_SCREEN)) {
            final Double value = Double.parseDouble(data.split("\t")[1]);
            final boolean isScreenLocked = value == 1;
            LOGGER.info(new StringBuilder().append("isScreenLocked: ")
                    .append(value).append(" -- ").append(isScreenLocked).toString());

            LightController.getInstance().setScreenLocked(isScreenLocked);
        }

    }

    /**
     * Called when a new websocket connection is accepted.
     *
     * @param connection The Connection object to use to send messages.
     */
    @Override
    public final void onOpen(final Connection connection) {
        LOGGER.info(new StringBuilder().append("onOpen -- ").append(PROTOCOL).toString());
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
        if (PROTOCOL.equals(WSocketClient.PROTOCOL_LIGHT_OUT)) {
            WSocketClient.getInstance().disconnect();
            WSocketClient.getInstance().restPing();
            WSocketClient.getInstance().connect();
        }
    }
}
