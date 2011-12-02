package eu.uberdust.communication.websocket;


import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;

import java.util.Arrays;


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
        LOGGER.info("BinaryMessage.Data (" + Arrays.toString(data) + ")");
        LOGGER.info("BinaryMessage.Data.Offset (" + offset + ")");
        LOGGER.info("BinaryMessage.Data.Length (" + length + ")");
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

    /**
     * On Text Message
     * @param data , data.
     */
    @Override
    public void onMessage(final String data) {
        LOGGER.info("TextMessage.Data (" + data + ")");
    }
}