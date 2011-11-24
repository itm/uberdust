package eu.uberdust.communication.websocket;

import eu.uberdust.communication.websocket.tasks.PingTask;
import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
    private final List<WebSocketClient> clients;

    /**
     * The WebSocket.Connection.
     */
    private final List<WebSocket.Connection> connections;

    /**
     * The protocol.
     */
    public static final String PROTOCOL_LIGHT_OUT = "urn:wisebed:ctitestbed:0xca3@urn:wisebed:node:capability:light";
    //public static final String PROTOCOL_LIGHT_IN = "urn:wisebed:ctitestbed:0x1cde@urn:wisebed:node:capability:light";
    public static final String PROTOCOL_LOCK_SCREEN = "urn:ctinetwork:black@urn:ctinetwork:node:capability:lockScreen";

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
        clients = new ArrayList<WebSocketClient>();
        connections = new ArrayList<WebSocket.Connection>();
        try {
            WS_URI = new URI("ws://uberdust.cti.gr:80/lastreading.ws");
            factory = new WebSocketClientFactory();
            factory.setBufferSize(4096);
            factory.start();

            /*final WebSocketClient clientLightIn = factory.newWebSocketClient();
            clientLightIn.setMaxIdleTime(-1);
            clientLightIn.setProtocol(PROTOCOL_LIGHT_IN);
            clients.add(clientLightIn);*/

            final WebSocketClient clientLightOut = factory.newWebSocketClient();
            clientLightOut.setMaxIdleTime(-1);
            clientLightOut.setProtocol(PROTOCOL_LIGHT_OUT);
            clients.add(clientLightOut);

            final WebSocketClient clientLockScreen = factory.newWebSocketClient();
            clientLockScreen.setMaxIdleTime(-1);
            clientLockScreen.setProtocol(PROTOCOL_LOCK_SCREEN);
            clients.add(clientLockScreen);

            connect();
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
            for (final WebSocketClient client : clients) {
                final WebSocket.Connection connection = client.open(WS_URI, new WebSocketIMPL(client.getProtocol())).get();
                connections.add(connection);
            }
            timer.scheduleAtFixedRate(new PingTask(timer), PingTask.DELAY, PingTask.DELAY);
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
        try {
            for (WebSocket.Connection connection : connections) {
                connection.sendMessage("ping");
            }
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    public static void main(final String[] args){
        WSocketClient.getInstance();
    }
}
