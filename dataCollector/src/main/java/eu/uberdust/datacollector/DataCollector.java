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
import java.util.Date;
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
    private static String[] Sensors_names, Sensors_prefixes;
    private static Map<String, String> sensors = new HashMap<String, String>();
    private static int messageCounter;
    private static Date lastTime;

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

        Sensors_names = properties.getProperty("sensors.names").split(",");
        Sensors_prefixes = properties.getProperty("sensors.prefixes").split(",");

        String Sens = "Sensors Checked: ";
        for (int i = 0; i < Sensors_names.length; i++) {
            Sens += Sensors_names[i] + "[" + Sensors_prefixes[i] + "]" + ",";
            sensors.put(Sensors_prefixes[i], Sensors_names[i]);
        }
        log.info(Sens);

        String[] device_types = properties.getProperty("device.Types").split(",");
        Sens = "Devices Monitored: ";
        for (String device_type : device_types) {
            Sens += device_type + ",";
        }
        log.info(Sens);
        messageCounter = 0;
        lastTime = new Date();
    }

    private final SimpleChannelUpstreamHandler upstreamHandler = new SimpleChannelUpstreamHandler() {

        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            Messages.Msg message = (Messages.Msg) e.getMessage();
            if (WSNApp.MSG_TYPE_LISTENER_MESSAGE.equals(message.getMsgType())) {
                WSNAppMessages.Message wsnAppMessage = WSNAppMessages.Message.parseFrom(message.getPayload());
                parse(wsnAppMessage.toString());
                messageCounter++;
                if (messageCounter == 1000) {
                    final long milis = new Date().getTime() - lastTime.getTime();
                    log.info(messageCounter + " messages in " + milis / 1000 + " sec");
                    lastTime = new Date();
                    messageCounter = 0;
                }

            } else {
                log.info("got a message of type " + message.getMsgType());
            }
        }

        private void parse(String toString) {
            (new Thread(new MessageParser(toString, sensors))).start();
        }
    };

    private final ChannelPipelineFactory channelPipelineFactory = new ChannelPipelineFactory() {

        @Override
        public ChannelPipeline getPipeline() throws Exception {

            ChannelPipeline p = pipeline();

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

        NioClientSocketChannelFactory factory =
                new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        bootstrap = new ClientBootstrap(factory);

        // Configure the event pipeline factory.
        bootstrap.setPipelineFactory(channelPipelineFactory);

        // Make a new connection.
        ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));

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
