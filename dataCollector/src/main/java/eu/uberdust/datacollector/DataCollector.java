package eu.uberdust.datacollector;

import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
import eu.uberdust.util.PropertyReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


/**
 * Opens a connection to a TestbedRuntime server and received debug messages from all nodes to collect data.
 */
public class DataCollector implements Runnable {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DataCollector.class);

    /**
     * WebSocket address prefix.
     */
    private static final String WS_URL_PREFIX = "ws://";

    /**
     * WebSocket address suffix.
     */
    private static final String WS_URL_SUFFIX = "insertreading.ws";
    /**
     * WebSocket address url.
     */
    private static String ws_url = "";

    /**
     * testbed hostname.
     */
    private transient String host;

    /**
     * testbed port to connect to.
     */
    private transient int port;

    /**
     * pipeline factory.
     */
    private transient NioClientSocketChannelFactory factory;
    private transient ClientBootstrap bootstrap;

    /**
     * Default Constructor.
     */
    public DataCollector() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));

        final StringBuilder wsUrlBuilder = new StringBuilder(WS_URL_PREFIX);
        wsUrlBuilder.append(PropertyReader.getInstance().getProperties().getProperty("uberdust.server"));
        wsUrlBuilder.append(":");
        wsUrlBuilder.append(PropertyReader.getInstance().getProperties().getProperty("uberdust.port"));
        wsUrlBuilder.append(PropertyReader.getInstance().getProperties().getProperty("uberdust.basepath"));
        wsUrlBuilder.append(WS_URL_SUFFIX);
        ws_url = wsUrlBuilder.toString();

        readProperties();

        connectWS();
    }

    /**
     * For WS implementation.
     * Connects to the WS server.
     */
    private void connectWS() {
        try {
            InsertReadingWebSocketClient.getInstance().connect(ws_url);
        } catch (Exception e) {
            LOGGER.fatal(e);
        }
    }

    /**
     * Reads the property file.
     */
    private void readProperties() {

        host = PropertyReader.getInstance().getProperties().getProperty("testbed.hostname");
        port = Integer.parseInt(PropertyReader.getInstance().getProperties().getProperty("testbed.overlay"));

    }


    /**
     * Channel factory with custom channelPipeline to parse the received messages.
     */
    private final transient DataCollectorPipelineFactory chPipelineFactory = new DataCollectorPipelineFactory(this);


    /**
     * Connects to testbedruntime overlay port to receive all incoming debug messages.
     *
     * @return true when connection was success
     */
    public final boolean start() {

        ChannelFuture connectFuture = null;
        // Make a new connection.
        connectFuture = bootstrap.connect(new InetSocketAddress(host, port));
        final Channel channel = connectFuture.getChannel();
        LOGGER.info(channel.getId());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOGGER.error(e);
        }

        // Wait until the connection is made successfully.
        if (!connectFuture.isSuccess()) {
            LOGGER.warn("Client Connect Failed!", connectFuture.getCause());
            return false;
        }
        return true;
    }

    /**
     * Reconnects to testbedruntime when connection was lost.
     */
    public final void restart() {
        LOGGER.info("Waiting for Testbed Restart...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOGGER.error(e);
        }
        LOGGER.info("Reconnecting...");
        if (!start()) {
            restart();
        }
    }

    @Override
    public void run() {
        factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(chPipelineFactory);

        if (!start()) {
            restart();
        }
    }
}
