package eu.uberdust.datacollector;

import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;


/**
 * Opens a connection to a TestbedRuntime server and received debug messages from all nodes to collect data.
 */
public class DataCollector {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DataCollector.class);
    /**
     * WebSocket address
     */
    private static final String WS_URL = "ws://uberdust.cti.gr:80/insertreading.ws";
    /**
     * Application property file name.
     */
    private static final String PROPERTY_FILE = "dataCollector.properties";


    /**
     * testbed hostname.
     */
    private transient String host;
    /**
     * testbed port to connect to.
     */
    private transient int port;
    /**
     * map of the names used in iSense application to capability names.
     */
    private final transient Map<String, String> sensors = new HashMap<String, String>();
    private NioClientSocketChannelFactory factory;

    /**
     * Default Constructor.
     */
    public DataCollector() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));

        readProperties();


        connectWS();

    }

    private void connectWS() {
        try {
            InsertReadingWebSocketClient.getInstance().connect(WS_URL);
        } catch (Exception e) {
            LOGGER.fatal(e);
        }
    }

    /**
     * Reads the property file.
     */
    private void readProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTY_FILE));
        } catch (IOException e) {
            LOGGER.error("No properties file found! dataCollector.properties not found!");
            return;
        }

        host = properties.getProperty("runtime.ipAddress");
        port = Integer.parseInt(properties.getProperty("runtime.port"));

        final String[] sensorsNames = properties.getProperty("sensors.names").split(",");
        final String[] sensorsPrefixes = properties.getProperty("sensors.prefixes").split(",");

        final StringBuilder sensBuilder = new StringBuilder("Sensors Checked: \n");
        for (int i = 0; i < sensorsNames.length; i++) {
            sensBuilder.append(sensorsNames[i]).append("[").append(sensorsPrefixes[i]).append("]").append("\n");
            sensors.put(sensorsPrefixes[i], sensorsNames[i]);
        }
        LOGGER.info(sensBuilder);

        final String[] deviceTypes = properties.getProperty("device.Types").split(",");
        final StringBuilder devBuilder = new StringBuilder("Devices Monitored: \n");
        for (String deviceType : deviceTypes) {
            devBuilder.append(deviceType).append("\n");
        }
        LOGGER.info(devBuilder);
    }


    /**
     * Channel factory with custom channelPipeline to parse the received messages.
     */
    private final transient DataCollectorPipelineFactory chPipelineFactory = new DataCollectorPipelineFactory(this);


    /**
     * Connects to testbedruntime overlay port to receive all incoming debug messages.
     */
    public final int start() {

        factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);

        chPipelineFactory.setSensors(sensors);

        // Configure the event pipeline factory.
        bootstrap.setPipelineFactory(chPipelineFactory);

        // Make a new connection.
        final ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));

        final Channel channel = connectFuture.awaitUninterruptibly().getChannel();
        LOGGER.debug(channel.getId());

        // Wait until the connection is made successfully.
        if (!connectFuture.isSuccess()) {
            LOGGER.error("client connect failed!", connectFuture.getCause());
            return 0;
        }
        return 1;

    }

    public final void restart() {
        factory.releaseExternalResources();
        while (0 == start()) {
            LOGGER.error("could not start sleeping 5000");
            System.exit(1);
        }

    }
}
