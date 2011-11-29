package eu.uberdust.communication.websocket;


import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;


/**
 * Implementation of InsertReading WebSocket.
 */
public class InsertReadingWebSocketIMPL implements WebSocket.OnBinaryMessage, WebSocket.OnTextMessage {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocketIMPL.class);


    /**
     * On Binary Message arrival.
     * @param data , data byte array.
     * @param offset , offset.
     * @param length , length/
     */
    @Override
    public void onMessage(final byte[] data, final int offset, final int length) {
        LOGGER.info("Data " + data.toString());
        LOGGER.info("offset " + offset);
        LOGGER.info("length " + length);
    }

    /**
     * On Text Message arrival.
     * @param data , data string.
     */
    @Override
    public void onMessage(final String data) {
        LOGGER.info("Data " + data.toString());
    }

    /**
     * On open connection.
     * @param connection connection instance.
     */
    @Override
    public void onOpen(final Connection connection) {
        LOGGER.info("onOpen");
    }

    /**
     * On close connection.
     * @param closeCode , close code.
     * @param message , on string message.
     */
    @Override
    public void onClose(final int closeCode, final String message) {
        LOGGER.info("onClose");
    }
}