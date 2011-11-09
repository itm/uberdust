package eu.uberdust.datacollector;

import com.google.protobuf.InvalidProtocolBufferException;
import de.uniluebeck.itm.gtr.messaging.Messages;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNApp;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNAppMessages;
import eu.wisebed.wisedb.HibernateUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
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
import java.util.concurrent.Executors;

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
     *
     */
       private static final int REPORT_LIMIT = 1000;
    /**
     * testbed hostname
     */
    private transient String host;
    /**
     * testbed port to connect to.
     */
    private transient int port;
    /**
     * map of the names used in iSense application to capability names.
     */
    private transient final Map<String, String> sensors = new HashMap<String, String>();
    /**
     * counts the messages received - stats.
     */
    private transient int messageCounter;
    /**
     * saves the last time 1000 messages were received - stats.
     */
    private transient long lastTime;

    public DataCollector() {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));

        // Initialize hibernate
        HibernateUtil.connectEntityManagers();

        readProperties();

        messageCounter = 0;
        lastTime = System.currentTimeMillis();
    }

    /**
     * Reads the property file.
     */
    private final void readProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("dataCollector.properties"));
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
    private transient final SimpleChannelUpstreamHandler upstreamHandler = new SimpleChannelUpstreamHandler() {

        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent messageEvent) throws InvalidProtocolBufferException {
            final Messages.Msg message = (Messages.Msg) messageEvent.getMessage();
            if (WSNApp.MSG_TYPE_LISTENER_MESSAGE.equals(message.getMsgType())) {
                final WSNAppMessages.Message wsnAppMessage = WSNAppMessages.Message.parseFrom(message.getPayload());
                parse(wsnAppMessage.toString());
                messageCounter++;
                if (messageCounter == REPORT_LIMIT) {
                    final long milliseconds = System.currentTimeMillis() - lastTime;
                    LOGGER.info("MessageRate : " + (double) messageCounter / (milliseconds / 1000) + " messages/sec");
                    lastTime = System.currentTimeMillis();
                    messageCounter = 0;
                }

            } else {
                LOGGER.error("got a message of type " + message.getMsgType());
            }
        }

        @Override
        public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent channelStateEvent) throws Exception {    // NOPMD
            super.channelDisconnected(ctx, channelStateEvent);
            LOGGER.error("channelDisconnected");
            System.exit(1);
        }


        private void parse(final String toString) {
            (new Thread(new MessageParser(toString, sensors))).start(); //NOPMD
        }
    };

    /**
     * Channel factory with custom channelPipeline to parse the received messages.
     */
    private transient final ChannelPipelineFactory chPipelineFactory = new ChannelPipelineFactory() {

        @Override
        public ChannelPipeline getPipeline() {

            final ChannelPipeline channelPipeline = pipeline();

            channelPipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
            channelPipeline.addLast("protobufEnvelopeMessageDecoder", new ProtobufDecoder(Messages.Msg.getDefaultInstance()));

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
        final NioClientSocketChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

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
