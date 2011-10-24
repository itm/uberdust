package eu.uberdust.datacollector;

import de.uniluebeck.itm.gtr.messaging.Messages;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNApp;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNAppMessages;
import org.apache.log4j.Level;
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

// Import log4j classes.

public class DataCollector {


    private static final Logger log = Logger.getLogger(DataCollector.class);
    private String host;
    private int port;
    private Channel channel;
    private ClientBootstrap bootstrap;
    private final Map<String, String> sensors = new HashMap<String, String>();
    private int messageCounter;
    private long lastTime;

    public DataCollector() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

        log.setLevel(Level.INFO);

        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("dataCollector.properties"));
        } catch (IOException e) {
            log.info("No properties file found! dataCollector.properties not found!");
            return;
        }

        host = properties.getProperty("runtime.ipAddress");
        port = Integer.parseInt(properties.getProperty("runtime.port"));

        final String[] sensors_names = properties.getProperty("sensors.names").split(",");
        final String[] sensors_prefixes = properties.getProperty("sensors.prefixes").split(",");

        final StringBuilder sensBuilder = new StringBuilder("Sensors Checked: ");
        for (int i = 0; i < sensors_names.length; i++) {
            sensBuilder.append(sensors_names[i]).append("[").append(sensors_prefixes[i]).append("]" + ",");
            sensors.put(sensors_prefixes[i], sensors_names[i]);
        }
        log.info(sensBuilder);

        final String[] device_types = properties.getProperty("device.Types").split(",");
        final StringBuilder devBuilder = new StringBuilder("Devices Monitored: ");
        for (String device_type : device_types) {
            devBuilder.append(device_type).append(",");
        }
        log.info(devBuilder);
        messageCounter = 0;
        lastTime = System.currentTimeMillis();
    }

    private final SimpleChannelUpstreamHandler upstreamHandler = new SimpleChannelUpstreamHandler() {

        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            final Messages.Msg message = (Messages.Msg) e.getMessage();
            if (WSNApp.MSG_TYPE_LISTENER_MESSAGE.equals(message.getMsgType())) {
                WSNAppMessages.Message wsnAppMessage = WSNAppMessages.Message.parseFrom(message.getPayload());
                parse(wsnAppMessage.toString());
                messageCounter++;
                if (messageCounter == 1000) {
                    final long milis = System.currentTimeMillis() - lastTime;
                    log.info(messageCounter + " messages in " + milis / 1000 + " sec");
                    lastTime = System.currentTimeMillis();
                    messageCounter = 0;
                }

            } else {
                log.info("got a message of type " + message.getMsgType());
            }
        }

        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            super.channelDisconnected(ctx, e);    //To change body of overridden methods use File | Settings | File Templates.
            log.info("channelDisconnected");
            System.exit(1);
        }


        private void parse(String toString) {
            (new Thread(new MessageParser(toString, sensors))).start();
        }
    };

    private final ChannelPipelineFactory channelPipelineFactory = new ChannelPipelineFactory() {

        @Override
        public ChannelPipeline getPipeline() throws Exception {

            final ChannelPipeline p = pipeline();

            p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
            p.addLast("protobufEnvelopeMessageDecoder", new ProtobufDecoder(Messages.Msg.getDefaultInstance()));

            p.addLast("frameEncoder", new LengthFieldPrepender(4));
            p.addLast("protobufEncoder", new ProtobufEncoder());

            p.addLast("handler", upstreamHandler);

            return p;

        }
    };


    //used to connect to testbedruntime
    public void start() {
        NioClientSocketChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        bootstrap = new ClientBootstrap(factory);

        // Configure the event pipeline factory.
        bootstrap.setPipelineFactory(channelPipelineFactory);

        // Make a new connection.
        final ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection is made successfully.
        channel = connectFuture.awaitUninterruptibly().getChannel();
        if (!connectFuture.isSuccess()) {
            log.error("client connect failed!", connectFuture.getCause());
        }
    }

    private void stop() {

        channel.close().awaitUninterruptibly();
        channel = null;

        bootstrap.releaseExternalResources();
        bootstrap = null;
    }
}
