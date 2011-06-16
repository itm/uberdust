package de.uniluebeck.itm.tr.runtime.socketconnector.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.util.Date;
import de.uniluebeck.itm.gtr.messaging.Messages;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNApp;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNAppMessages;
import de.uniluebeck.itm.tr.util.Logging;
import de.uniluebeck.itm.tr.util.StringUtils;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

public class SocketConnectorClient {

    private static Logger log;
    private String host;
    private int port;
    private Channel channel;
    private ClientBootstrap bootstrap;
    private static String connectionURL;
    private static Connection connection;
    private static Statement statement;

    public SocketConnectorClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {



        connectionURL = "jdbc:mysql://150.140.5.11:3306/dataCollector";
        connection = null;
        statement = null;
        try {
            // Load JBBC driver "com.mysql.jdbc.Driver".
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(connectionURL, "testbedruntime", "");
            statement = connection.createStatement();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        // set up logging
        Logging.setLoggingDefaults();
        log = LoggerFactory.getLogger(SocketConnectorClient.class);

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

        String ipAddress = "hercules.cti.gr";
        int port = 1234;

        try {

            CommandLine line = parser.parse(options, args);

            if (line.hasOption('v')) {
                org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
            }

            if (line.hasOption('l')) {
                Level level = Level.toLevel(line.getOptionValue('l'));
                System.out.println("Setting log level to " + level);
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

        SocketConnectorClient client = new SocketConnectorClient(ipAddress, port);
        client.start();

    }
    private SimpleChannelUpstreamHandler upstreamHandler = new SimpleChannelUpstreamHandler() {

        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {

            Messages.Msg message = (Messages.Msg) e.getMessage();

            if (WSNApp.MSG_TYPE_LISTENER_MESSAGE.equals(message.getMsgType())) {

                WSNAppMessages.Message wsnAppMessage = WSNAppMessages.Message.parseFrom(message.getPayload());

                //log.info("Received sensor node binary output: \"{}\"",  );
                parse(wsnAppMessage.toString());
                //System.out.println(wsnAppMessage.toString());

            } else {
                log.info("Received message: {}", StringUtils.jaxbMarshal(message));
            }
        }

        private void parse(String toString) {

            final Date nowtime = new Date();
            final String strLine = toString;

            try {
                if (strLine.contains("EM_T")) {
                    final long milis = nowtime.getTime();

                    final int start_bidis = strLine.indexOf("BIDIS ") + 6;
                    final int end_bidis = strLine.indexOf(" ", start_bidis);
                    int bidis = -1;
                    try {
                        bidis = Integer.parseInt(strLine.substring(start_bidis, end_bidis));
                    } catch (Exception e) {
                    }

                    final int start_lqi = strLine.indexOf("LQI ") + 4;
                    final int end_lqi = strLine.indexOf(" ", start_lqi);
                    int lqi = -1;
                    try {
                        lqi = Integer.parseInt(strLine.substring(start_lqi, end_lqi));
                    } catch (Exception e) {
                    }

                    String node_id = "";
                    if (strLine.contains("iSense::")) {
                        node_id = strLine.substring(strLine.indexOf("iSense::") + 8, strLine.indexOf(" ", strLine.indexOf("iSense::")));
                    } else if (strLine.contains("telosB::")) {
                        node_id = strLine.substring(strLine.indexOf("telosB::") + 8, strLine.indexOf(" ", strLine.indexOf("telosB::")));
                    }
                    final int start_temp = strLine.indexOf("EM_T ") + 5;
                    final int end_temp = strLine.indexOf(" ", start_temp);
                    int temp = -200;
                    try {
                        temp = Integer.parseInt(strLine.substring(start_temp, end_temp));
                    } catch (Exception e) {
                    }

                    final int start_lux = strLine.indexOf("EM_L ") + 5;
                    final int end_lux = strLine.indexOf("\"", start_lux) - 1;
                    int lux = -1;
                    try {
                        lux = Integer.parseInt(strLine.substring(start_lux, end_lux));
                    } catch (Exception e) {
                    }

                    final int start_humid = strLine.indexOf("EM_H ") + 5;
                    final int end_humid = strLine.indexOf(" ", start_humid);
                    int humid = -1;
                    try {
                        humid = Integer.parseInt(strLine.substring(start_humid, end_humid));
                    } catch (Exception e) {
                    }

                    final int start_inflight = strLine.indexOf("EM_I ") + 5;
                    final int end_inflight = strLine.indexOf(" ", start_inflight);
                    int inflight = -1;
                    try {
                        inflight = Integer.parseInt(strLine.substring(start_inflight, end_inflight));
                    } catch (Exception e) {
                    }



                    // send_temp(node_id, temp, milis);
                    // send_lux(node_id, lux, milis);
                    // send_bidis(node_id, bidis, milis);
                    // send_lqi(node_id, lqi, milis);                



                    if (temp != -200) {
                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Temperature'," + temp + "," + milis + ")");
                    }
                    if (lux != -1) {
                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Light'," + lux + "," + milis + ")");
                    }
                    if (bidis > -1) {
                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Neighbors'," + bidis + "," + milis + ")");
                    }
                    if (lqi > -1) {
                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Lqi'," + lqi + "," + milis + ")");
                    }
                    if (humid > -1) {
                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Humidity'," + humid + "," + milis + ")");
                    }
                    if (inflight > -1) {
                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Infrared'," + inflight + "," + milis + ")");
                    }
                    statement.executeBatch();
                    statement.clearBatch();

                    System.out.println(node_id + " saved values to database");

                } else if (strLine.contains("airquality::")) {

                    String type = "";
                    if (strLine.contains("SVal1:")) {
                        type = "CO2";
                    } else if (strLine.contains("SVal2:")) {
                        type = "CO";
                    } else if (strLine.contains("SVal3:")) {
                        type = "CH4";
                    }
                    if (!type.equals("")) {
                        final int start_value = strLine.indexOf("SVal") + 6;
                        final int end_value = strLine.indexOf(" ", start_value);
                        final int value = Integer.parseInt(strLine.substring(start_value, end_value));


                        String node_id = "";
                        if (strLine.contains("airquality::")) {
                            node_id = strLine.substring(strLine.indexOf("airquality::") + 12, strLine.indexOf(" ", strLine.indexOf("airquality::")));
                        }


                        connectionURL = "jdbc:mysql://150.140.5.11:3306/dataCollector";
                        connection = null;
                        statement = null;

                        // Load JBBC driver "com.mysql.jdbc.Driver".                
                        Class.forName("com.mysql.jdbc.Driver").newInstance();
                        connection = DriverManager.getConnection(connectionURL, "testbedruntime", "isensectitelosb");
                        statement = connection.createStatement();

                        final long milis = nowtime.getTime();

                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','" + type + "'," + value + "," + milis + ")");

                        statement.executeBatch();
                        statement.clearBatch();

                        System.out.println(node_id + " saved values to database");

                        //statement.close();
                        //connection.close();


                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }

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
        formatter.printHelp(120, SocketConnectorClient.class.getCanonicalName(), null, options, null);
        System.exit(
                1);
    }
}
