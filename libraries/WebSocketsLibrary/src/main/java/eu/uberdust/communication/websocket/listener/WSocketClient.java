package eu.uberdust.communication.websocket.listener;

import eu.uberdust.communication.rest.InsertReadingRestClient;
import eu.uberdust.communication.websocket.listener.tasks.PingTask;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Observable;
import java.util.Timer;


/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/12/11
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public final class WSocketClient extends Observable {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(WSocketClient.class);

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static WSocketClient ourInstance = null;
    /**
     * Websocket url prefix.
     */
    private static final String WS_PREFIX = "ws://";
    /**
     * Http url prefix.
     */
    private static final String HTTP_PREFIX = "http://";

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
    private String protocol = "";


    private final WebSocketIMPL webSocketIMPL = new WebSocketIMPL();
    /**
     * The timer.
     */
    private Timer timer;
    private String webSocketUrl;
    private String serverUrl;

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
    }

    public void start() {
        LOGGER.info("WSocketClient initialized");
        timer = new Timer();
        connect();
        timer.scheduleAtFixedRate(new PingTask(timer), PingTask.DELAY, PingTask.DELAY);
    }


    public void setServer(String serverUrl) {
        this.serverUrl = serverUrl;
        webSocketUrl = "ws://" + serverUrl + "/lastreading.ws";
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Connects to the WebSocket.
     */
    public void connect() {
        try {
            WS_URI = new URI(webSocketUrl);
            factory = new WebSocketClientFactory();
            factory.setBufferSize(4096);
            factory.start();

            client = factory.newWebSocketClient();
            client.setMaxIdleTime(-1);
            client.setProtocol(protocol);
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


    public void restPing() {
        InsertReadingRestClient.getInstance().callRestfulWebService(webSocketUrl.replace(WS_PREFIX, HTTP_PREFIX));
    }

    protected void update(String data) {
        this.setChanged();
        this.notifyObservers(data);
    }

}
