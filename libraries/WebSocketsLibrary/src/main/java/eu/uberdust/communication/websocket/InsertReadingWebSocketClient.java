package eu.uberdust.communication.websocket;

import eu.uberdust.communication.websocket.task.PingTask;
import eu.uberdust.reading.LinkReading;
import eu.uberdust.reading.NodeReading;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
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
     * Timer
     */
    private Timer timer;

    /**
     * The WebSocketClient.
     */
    private WebSocketClient client;

    /**
     * The WebSocket Connection.
     */
    private WebSocket.Connection connection;

    /**
     * WebSocketClientFactory,
     */
    private WebSocketClientFactory factory;

    /**
     * WSocketClient is loaded on the first execution of WSocketClient.getInstance()
     * or the first access to WSocketClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static InsertReadingWebSocketClient getInstance() {
        synchronized (InsertReadingWebSocketClient.class) {
            if (ourInstance == null) {
                ourInstance = new InsertReadingWebSocketClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private InsertReadingWebSocketClient() {
        factory = new WebSocketClientFactory();
        factory.setBufferSize(4096);
        try {
            factory.start();
            client = factory.newWebSocketClient();
            client.setMaxIdleTime(-1);
            client.setProtocol(PROTOCOL);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Connects to the WebSocket.
     *
     * @param webSocketUrl WebSocket URL.
     * @throws java.io.IOException         an IOException exception.
     * @throws java.net.URISyntaxException a URI SyntaxException.
     * @throws InterruptedException        InterruptedException exception.
     * @throws java.util.concurrent.ExecutionException
     *                                     ExecutionException exception.
     */
    public void connect(final String webSocketUrl) throws IOException, URISyntaxException,
            ExecutionException, InterruptedException {
        try {

            // open connection
            connection = client.open(new URI(webSocketUrl), new InsertReadingWebSocketIMPL()).get();

            startPingingTask();
        } catch (final Exception e) {

            // in case of exception keep trying to make connection after 2 seconds
            LOGGER.error(e);
            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e1) {
                LOGGER.error(e1);
            }
            connect(webSocketUrl);
        }
    }

    /**
     * Send Node Reading.
     *
     * @param nodeReading a NodeReading instance.
     * @throws java.io.IOException an IOException exception.
     */
    public void sendNodeReading(final NodeReading nodeReading) throws IOException {
        sendMessage(nodeReading.toDelimitedString());
    }

    /**
     * Send Link Reading.
     *
     * @param linkReading a NodeReading instance.
     * @throws java.io.IOException an IOException exception.
     */
    public void sendLinkReading(final LinkReading linkReading) throws IOException {
        sendMessage(linkReading.toDelimitedString());
    }

    /**
     * Send message over the WebSocket as binary data.
     *
     * @param message a string message.
     * @throws java.io.IOException an IOException.
     */
    private void sendMessage(final String message) throws IOException {

        // if connection is not opened do nothing
        if (!connection.isOpen()) {
            return;
        }

        byte[] bytes = message.getBytes();
        connection.sendMessage(bytes, 0, bytes.length);
    }

    /**
     * Start pinging task.
     */
    private void startPingingTask() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.scheduleAtFixedRate(new PingTask(), PingTask.DELAY, PingTask.DELAY);
    }

    /**
     * Stop pinging task.
     */
    private void stopPingingTask() {
        timer.cancel();
    }

    /**
     * Send PING message as string data to keep connection alive.
     */
    public void ping() {

        // if connection is not opened do nothing
        if (!connection.isOpen()) {
            return;
        }

        try {
            connection.sendMessage("PING");
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Disconnect method.
     */
    public void disconnect() {
        try {
            stopPingingTask();
            connection.disconnect();
            if (factory.isRunning()) {
                factory.stop();
            }
            factory.destroy();
        } catch (final Exception e) {
            LOGGER.error(e);
        }
    }
}
