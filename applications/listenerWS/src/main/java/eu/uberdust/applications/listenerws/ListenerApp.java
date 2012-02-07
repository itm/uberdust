package eu.uberdust.applications.listenerws;

import eu.uberdust.communication.websocket.listener.WSocketClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 2/7/12
 * Time: 1:51 PM
 */
public class ListenerApp implements Observer {
    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ListenerApp.class);

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static ListenerApp ourInstance = null;
    private String server;
    private String protocol;

    public static ListenerApp getInstance() {
        synchronized (ListenerApp.class) {
            if (ourInstance == null) {
                ourInstance = new ListenerApp();
            }
        }
        return ourInstance;
    }

    public ListenerApp() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("hello world!");
        server = "uberdust.cti.gr:80";
        protocol = "urn:wisebed:ctitestbed:0x9979@urn:wisebed:node:capability:pir";
        server = "qopbot.dyndns.org:8081";
        protocol = "urn:qopbot:destiny@urn:qopbot:node:capability:sdb:temperature";

        WSocketClient.getInstance().setServer(server);
        WSocketClient.getInstance().setProtocol(protocol);
        LOGGER.info("Starting connection with Server:" + server);
        LOGGER.info("Starting connection with protocol:" + protocol);

        WSocketClient.getInstance().start();
        WSocketClient.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof WSocketClient)) {
            return;
        }
        if (!(arg instanceof String)) {
            return;
        }

        final String reading = (String) arg;
        LOGGER.info(reading);
    }
}
