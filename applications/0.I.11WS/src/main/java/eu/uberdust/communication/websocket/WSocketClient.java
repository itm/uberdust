package eu.uberdust.communication.websocket;

import eu.uberdust.communication.websocket.tasks.PingTask;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;


/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/12/11
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public final class WSocketClient {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(WSocketClient.class);

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static WSocketClient ourInstance = null;

    /**
     * Static WebSocket URI.
     */
    private URI WS_URI;

    /**
     * The WebSocketClientFactory.
     */
    private WebSocketClientFactory factory;

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
    private static final String PROTOCOL = "urn:wisebed:ctitestbed:0x1ccd@urn:wisebed:node:capability:pir";


    private final WebSocketIMPL webSocketIMPL = new WebSocketIMPL();
    /**
     * The timer.
     */
    private final Timer timer;

    /**
     * WSocketClient is loaded on the first execution of WSocketClient.getInstance()
     * or the first access to WSocketClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static WSocketClient getInstance() {
        synchronized (WSocketClient.class) {
            if (ourInstance == null) {
                ourInstance = new WSocketClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private WSocketClient() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("WSocketClient initialized");
        timer = new Timer();
        connect();
        timer.scheduleAtFixedRate(new PingTask(timer), PingTask.DELAY, PingTask.DELAY);

    }

    /**
     * Connects to the WebSocket.
     */
    public void connect() {
        try {
            WS_URI = new URI("ws://uberdust.cti.gr:80/lastreading.ws");
            factory = new WebSocketClientFactory();
            factory.setBufferSize(4096);
            factory.start();

            client = factory.newWebSocketClient();
            client.setMaxIdleTime(-1);
            client.setProtocol(PROTOCOL);
            connection = client.open(WS_URI, webSocketIMPL).get();

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

    public void ping() {
        if (!connection.isOpen())
            return;

        try {
            connection.sendMessage("ping");
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    public void disconnect() {
        try {
            connection.disconnect();
            if (factory.isRunning()) {
                factory.destroy();
            }
            factory.stop();
        } catch (final Exception e) {
            LOGGER.error(e);
        }
    }


}
