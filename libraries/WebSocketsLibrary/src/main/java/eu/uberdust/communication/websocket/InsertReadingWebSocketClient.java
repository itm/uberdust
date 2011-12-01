package eu.uberdust.communication.websocket;

import eu.uberdust.eu.uberdust.reading.LinkReading;
import eu.uberdust.eu.uberdust.reading.NodeReading;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

/**
 * Insert New Reading Web Socket Client.
 */
public final class InsertReadingWebSocketClient {

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static InsertReadingWebSocketClient ourInstance = null;

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWebSocketClient.class);


    /**
     * Web Socket Protocol.
     */
    private static final String PROTOCOL = "INSERTREADING";

    /**
     * The WebSocketClient.
     */
    private WebSocketClient client;

    /**
     * The WebSocket Connection.
     */
    private WebSocket.Connection connection;

    /**
     * WSocketClient is loaded on the first execution of WSocketClient.getInstance()
     * or the first access to WSocketClient.ourInstance, not before.
     *
     * @return ourInstance
     * @throws Exception an Exception exception.
     */
    public static InsertReadingWebSocketClient getInstance() throws Exception {
        synchronized (InsertReadingWebSocketClient.class) {
            if (ourInstance == null) {
                ourInstance = new InsertReadingWebSocketClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     *
     * @throws Exception an Exception exception
     */
    private InsertReadingWebSocketClient() throws Exception {
        WebSocketClientFactory factory = new WebSocketClientFactory();
        factory.setBufferSize(4096);
        factory.start();
        client = factory.newWebSocketClient();
        client.setMaxIdleTime(-1);
        client.setProtocol(PROTOCOL);
    }

    /**
     * Connects to the WebSocket.
     *
     * @param webSocketUrl WebSocket URL.
     * @throws java.io.IOException an IOException exception.
     * @throws java.net.URISyntaxException  a URI SyntaxException.
     * @throws InterruptedException InterruptedException exception.
     * @throws java.util.concurrent.ExecutionException ExecutionException exception.
     */
    public void connect(final String webSocketUrl) throws IOException, URISyntaxException,
            ExecutionException, InterruptedException {
        connection = client.open(new URI(webSocketUrl), new InsertReadingWebSocketIMPL()).get();
    }

    /**
     * Send Node Reading.
     *
     * @param nodeReading a NodeReading instance.
     * @throws java.io.IOException an IOException exception.
     */
    public void sendNodeReading(final NodeReading nodeReading) throws IOException {
        sendMessage(nodeReading.toString());
    }

    /**
     * Send Link Reading.
     *
     * @param linkReading a NodeReading instance.
     * @throws java.io.IOException an IOException exception.
     */
    public void setLinkReading(final LinkReading linkReading) throws IOException {
        sendMessage(linkReading.toString());
    }

    /**
     * Send message over the WebSocket.
     * @param message a string message.
     * @throws java.io.IOException an IOException.
     */
    private void sendMessage(final String message) throws IOException {
        byte[] bytes = message.getBytes();
        connection.sendMessage(bytes, 0, bytes.length);
    }
}
