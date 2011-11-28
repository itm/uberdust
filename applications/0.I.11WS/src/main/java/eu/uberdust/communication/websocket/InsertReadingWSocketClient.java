package eu.uberdust.communication.websocket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Insert New Reading Web Socket Client.
 */
public final class InsertReadingWSocketClient {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InsertReadingWSocketClient.class);

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static InsertReadingWSocketClient ourInstance = null;

    /**
     * Static WebSocket URI.
     */
    private URI webSocketUri;

    /**
     * The WebSocketClient.
     */
    private WebSocketClient client;

    /**
     * The WebSocket.Connection.
     */
    private WebSocket.Connection connection = null;

    /**
     * The protocol.
     */
    private static final String PROTOCOL = "INSERTREADING";

    /**
     * WSocketClient is loaded on the first execution of WSocketClient.getInstance()
     * or the first access to WSocketClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static InsertReadingWSocketClient getInstance() {
        synchronized (InsertReadingWSocketClient.class) {
            if (ourInstance == null) {
                ourInstance = new InsertReadingWSocketClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private InsertReadingWSocketClient() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("WSocketClient initialized");
        try {
            webSocketUri = new URI("ws://uberdust.cti.gr:80/insertreading.ws");
            WebSocketClientFactory factory = new WebSocketClientFactory();
            factory.setBufferSize(4096);
            factory.start();
            client = factory.newWebSocketClient();
            client.setMaxIdleTime(-1);
            client.setProtocol(PROTOCOL);
            connect();
            connection.sendMessage("Connection");
        } catch (final URISyntaxException e) {
            LOGGER.error(e);
        } catch (final Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Connects to the WebSocket.
     */
    public void connect() {
        try {
            connection = client.open(webSocketUri, new WebSocketIMPL()).get();
        } catch (final Exception e) {
            LOGGER.error(e);
            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e1) {
                LOGGER.error(e1);
            }
            connect();
        }
    }

    /**
     * Sending PING.
     */
    public void ping() {
        try {
            connection.sendMessage("PING");
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Main.
     *
     * @param args args
     */
    public static void main(final String args[]) {
        InsertReadingWSocketClient.getInstance();
    }
}
