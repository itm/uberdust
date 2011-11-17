package eu.uberdust.datacollector;

import com.google.protobuf.InvalidProtocolBufferException;
import de.uniluebeck.itm.gtr.messaging.Messages;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNApp;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNAppMessages;
import eu.wisebed.wisedb.HibernateUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.jboss.netty.channel.Channels.pipeline;


/**
 * Opens a connection to a TestbedRuntime server and received debug messages from all nodes to collect data.
 */
public class DataCollector {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DataCollector.class);
    /**
     * Application property file name.
     */
    private static final String PROPERTY_FILE = "dataCollector.properties";

    /**
     *
     */
    private static final int REPORT_LIMIT = 1000;
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
    /**
     * counts the messages received - stats.
     */
    private transient int messageCounter;
    /**
     * saves the last time 1000 messages were received - stats.
     */
    private transient long lastTime;
    /**
     * executors for handling incoming messages.
     */
    private final transient ExecutorService executorService;


    /**
     * Default Constructor.
     */
    public DataCollector() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));

        // Initialize hibernate
        HibernateUtil.connectEntityManagers();

        readProperties();

        messageCounter = 0;
        lastTime = System.currentTimeMillis();

        executorService = Executors.newCachedThreadPool();

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
     * Chanel handler that receives the messages and Generates parser threads.
     */
    private final transient SimpleChannelUpstreamHandler upstreamHandler = new SimpleChannelUpstreamHandler() {

        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent messageEvent)
                throws InvalidProtocolBufferException {
            final Messages.Msg message = (Messages.Msg) messageEvent.getMessage();
            if (WSNApp.MSG_TYPE_LISTENER_MESSAGE.equals(message.getMsgType())) {
                final WSNAppMessages.Message wsnAppMessage = WSNAppMessages.Message.parseFrom(message.getPayload());
                parse(wsnAppMessage.toString());
                messageCounter++;
                if (messageCounter == REPORT_LIMIT) {
                    final long milliseconds = System.currentTimeMillis() - lastTime;
                    LOGGER.info("MessageRate : " + messageCounter / (milliseconds / (double) REPORT_LIMIT) + " messages/sec");
                    final ThreadPoolExecutor pool = (ThreadPoolExecutor) executorService;
                    LOGGER.info("PoolSize : " + pool.getPoolSize() + " Active :" + pool.getActiveCount());
                    LOGGER.info("Peak : " + pool.getLargestPoolSize());

                    lastTime = System.currentTimeMillis();
                    messageCounter = 0;
                }

            } else {
                LOGGER.error("got a message of type " + message.getMsgType());
            }
        }

        /**
         *
         * @param ctx
         * @param channelStateEvent
         * @throws Exception
         */
        @Override
        public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent channelStateEvent) throws Exception {     //NOPMD
            super.channelDisconnected(ctx, channelStateEvent);
            LOGGER.error("channelDisconnected");
            System.exit(1);
        }

        /**
         *
         * @param toString
         */
        private void parse(final String toString) {
            executorService.submit(new MessageParser(toString, sensors));
        }
    };

    /**
     * Channel factory with custom channelPipeline to parse the received messages.
     */
    private final transient ChannelPipelineFactory chPipelineFactory = new ChannelPipelineFactory() {

        @Override
        public ChannelPipeline getPipeline() {

            final ChannelPipeline channelPipeline = pipeline();

            channelPipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
            channelPipeline.addLast("pbfEnvelopeMessageDec", new ProtobufDecoder(Messages.Msg.getDefaultInstance()));

            channelPipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
            channelPipeline.addLast("protobufEncoder", new ProtobufEncoder());

            channelPipeline.addLast("handler", upstreamHandler);

            return channelPipeline;

        }
    };


    /**
     * Connects to testbedruntime overlay port to receive all incoming debug messages.
     */
    public final void start() {
        NioClientSocketChannelFactory factory = null;
        factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);

        // Configure the event pipeline factory.
        bootstrap.setPipelineFactory(chPipelineFactory);

        // Make a new connection.
        final ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));

        final Channel channel = connectFuture.awaitUninterruptibly().getChannel();
        LOGGER.debug(channel.getId());

        // Wait until the connection is made successfully.
        if (!connectFuture.isSuccess()) {
            LOGGER.error("client connect failed!", connectFuture.getCause());
        }
    }
}
