package eu.uberdust.datacollector;

import de.uniluebeck.itm.gtr.messaging.Messages;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNApp;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNAppMessages;
import org.apache.commons.cli.*;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

// Import log4j classes.

public class testbedListener {

    private static Logger log = Logger.getLogger(testbedListener.class);
    private String host;
    private int port;
    private Channel channel;
    private ClientBootstrap bootstrap;
    private static Properties properties;
    private static String[] Sensors_names, Sensors_prefixes;
    private static String[] device_types;
    private static String capability_prefix = "urn:wisebed:node:capability:";


    public testbedListener(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public static void main(String[] args) throws IOException {

        //BasicConfigurator.configure();
        PropertyConfigurator.configure("classes/log4j.properties");

        log.setLevel(Level.INFO);


        properties = new Properties();
        try {
            properties.load(new FileInputStream("classes/dataCollector.properties"));
        } catch (IOException e) {
            log.info("No properties file found! dataCollector.properties not found!");
            return;
        }


//        log.info("hibernate connected");


        String ipAddress = properties.getProperty("runtime.ipAddress");
        int port = Integer.parseInt(properties.getProperty("runtime.port"));


        Sensors_names = properties.getProperty("sensors.names").split(",");
        Sensors_prefixes = properties.getProperty("sensors.prefixes").split(",");

        String Sens = "Sensors Acknoledging: ";
        for (int i = 0; i < Sensors_names.length; i++) {
            Sens += Sensors_names[i] + "[" + Sensors_prefixes[i] + "]" + ",";
        }
        log.info(Sens);

        device_types = properties.getProperty("device.Types").split(",");
        Sens = "Devices Monitored: ";
        for (int i = 0; i < device_types.length; i++) {
            Sens += device_types[i] + ",";
        }
        log.info(Sens);

        // create the command line parser
        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        options.addOption("i", "ip", true, "The IP address of the host to connect to");
        options.addOption("p", "port", true, "The port number of the host to connect to");

        options.addOption("v", "verbose", false, "Verbose logging output (equal to -l DEBUG)");
        options.addOption("l", "logging", true,
                "Set logging level (one of [" + Level.TRACE + "," + Level.DEBUG + "," + Level.INFO + ","
                        + Level.WARN + "," + Level.ERROR + "])");

        options.addOption("h", "help", false, "Help output");


        try {

            CommandLine line = parser.parse(options, args);

            if (line.hasOption('v')) {
                org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
            }

            if (line.hasOption('l')) {
                Level level = Level.toLevel(line.getOptionValue('l'));
                org.apache.log4j.Logger.getRootLogger().setLevel(level);
                org.apache.log4j.Logger.getLogger("de.uniluebeck.itm").setLevel(level);
            }

            if (line.hasOption('h')) {
                usage(options);
            }

            if (line.hasOption('p')) {
                try {
                    port = Integer.parseInt(line.getOptionValue('p'));
                } catch (NumberFormatException e) {
                    throw new Exception("Port number must be a valid integer between 0 and 65536");
                }
            }

        } catch (Exception e) {
            log.error("Invalid command line: " + e, e);
            usage(options);
        }

        testbedListener client = new testbedListener(ipAddress, port);
        client.start();


    }

    private SimpleChannelUpstreamHandler upstreamHandler = new SimpleChannelUpstreamHandler() {

        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            Messages.Msg message = (Messages.Msg) e.getMessage();
            if (WSNApp.MSG_TYPE_LISTENER_MESSAGE.equals(message.getMsgType())) {
                WSNAppMessages.Message wsnAppMessage = WSNAppMessages.Message.parseFrom(message.getPayload());
//                log.info("Received sensor node binary output: \"{}\"",  );
                parse(wsnAppMessage.toString());
            } else {
                log.info("got a message of type " + message.getMsgType());
            }


        }


        private void parse(String toString) {
            MessageParser msgp = new MessageParser(toString, Sensors_names, Sensors_prefixes);
            msgp.run();

        }
    };

    private ChannelPipelineFactory channelPipelineFactory = new ChannelPipelineFactory() {

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
    private void start() {

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

    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, testbedListener.class.getCanonicalName(), null, options, null);
        System.exit(1);
    }
}
