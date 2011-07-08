package de.uniluebeck.itm.tr.runtime.socketconnector.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.util.Date;

import com.google.common.util.concurrent.UninterruptibleFuture;
import com.sun.org.apache.bcel.internal.generic.GotoInstruction;
import de.uniluebeck.itm.gtr.messaging.Messages;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNApp;
import de.uniluebeck.itm.tr.runtime.wsnapp.WSNAppMessages;
import de.uniluebeck.itm.tr.util.Logging;
import de.uniluebeck.itm.tr.util.MySQLConnection;
import de.uniluebeck.itm.tr.util.PropertiesUtils;
import de.uniluebeck.itm.tr.util.StringUtils;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.bouncycastle.crypto.RuntimeCryptoException;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.util.internal.SystemPropertyUtil;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.Properties;
import java.io.FileInputStream;

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
    private static String db_username;
    private static String db_password;
    private static Properties properties;
    private static String[] Sensors_names, Sensors_prefixes;
    private static String[] device_types;


    public SocketConnectorClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public static void main(String[] args) throws IOException {

        properties = new Properties();
        try {
            properties.load(new FileInputStream("dataCollector.properties"));
        } catch (IOException e) {
            log.info("No properties file found! dataCollector.properties not found!");
            return;
        }


        //connectionURL = "jdbc:mysql://150.140.5.11:3306/dataCollector";
        connectionURL = properties.getProperty("mysql.url");
        //log.info(connectionURL);
        db_username = properties.getProperty("mysql.username");
        //log.info(db_username);
        db_password = properties.getProperty("mysql.password");
        //log.info(db_password);
        String ipAddress = properties.getProperty("runtime.ipAddress");
        int port = Integer.parseInt(properties.getProperty("runtime.port"));


        // set up logging
        Logging.setLoggingDefaults();
        log = LoggerFactory.getLogger(SocketConnectorClient.class);


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

        connection = null;
        statement = null;


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


        private String extractNodeId(String linea) {
            final String line = linea.substring(7);
            final int start = line.indexOf("0x");
            if (start > 0) {
                final int end = line.indexOf(" ", start);
                if (end > 0) {
                    //System.out.println(line);
                    //System.out.println(line.substring(start, end));
                    return line.substring(start, end);
                }
            }
            return "";
        }

        private String extractSenderId(String linea) {
            if (linea.contains("Source")) {
                final String line = linea.substring(7);
                final int start = line.indexOf("Source0x") + 6;
                if (start > 0) {
                    final int end = line.indexOf(" ", start);
                    if (end > 0) {
                        //System.out.println(line);
                        //System.out.println(line.substring(start, end));
                        return line.substring(start, end);
                    }
                }
            }
            return "";
        }


        private void parse(String toString) {

            try {
                // Load JBBC driver "com.mysql.jdbc.Driver".
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection(connectionURL, db_username, db_password);
                statement = connection.createStatement();
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }

            final long milis = (new Date()).getTime();
            final String strLine = toString.substring(toString.indexOf("binaryData:") + "binaryData:".length());

            //log.info(strLine);
            final String node_id = extractNodeId(strLine);
            final String sender_id = extractSenderId(strLine);
            log.info(node_id + "    ----    " + sender_id);
            try {

                if (node_id != "") {
                    for (int i = 0; i < Sensors_prefixes.length; i++) {
                        final int start = strLine.indexOf(Sensors_prefixes[i]) + Sensors_prefixes[i].length() + 1;
                        if (start > Sensors_prefixes[i].length()) {
                            int end = strLine.indexOf(" ", start);
                            if (end == -1) {
                                end = strLine.length() - 2;
                            }
                            int value = -1;
                            try {
                                value = Integer.parseInt(strLine.substring(start, end));
                                log.info("Got a value " + value + " for " + Sensors_names[i]);
                                if ((value > -1) && (value < 10000)) {
                                    statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','" + Sensors_names[i] + "'," + value + "," + milis + ")");
                                    //System.out.println("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','"+Sensors_names[i]+"'," + value + "," + milis + ")");
                                }
                            } catch (Exception e) {
                                log.error("Cannot parse value for " + Sensors_prefixes[i] + "'" + strLine.substring(start, end) + "'");
                            }
                        }
                    }
                }

                if (!sender_id.equals("")) {
                    final int start = strLine.indexOf("LQI") + "LQI".length() + 1;
                    if (start > "LQI".length()) {
                        int end = strLine.indexOf(" ", start);
                        if (end == -1) {
                            end = strLine.length() - 2;
                        }
                        int value = -1;
                        try {
                            value = Integer.parseInt(strLine.substring(start, end));
                            if ((value > -1) && (value < 10000)) {
                                log.info("Got a link " + node_id + "<->" + sender_id + " lqi " + value);
                                statement.addBatch("INSERT INTO link (id,nodeidA,nodeidB,quality,time) VALUES (NULL,'" + node_id.substring(2) + "','" + sender_id.substring(2) + "'," + value + "," + milis + ")");
                                //System.out.println("INSERT INTO link (id,nodeidA,nodeidB,quality,time) VALUES (NULL,'" + node_id.substring(2) + "','" + sender_id.substring(2) + "'," + value + "," + milis + ")");
                            }
                        } catch (Exception e) {
                            log.error("Cannot parse lqi link value for " + node_id + "'" + strLine.substring(start, end) + "'");
                        }
                    }

                }

                statement.executeBatch();
                statement.clearBatch();
                log.info("Saved values for " + node_id);

                statement.close();
                connection.close();

//
//
//                    final int start_bidis = strLine.indexOf("BIDIS ") + 6;
//                    final int end_bidis = strLine.indexOf(" ", start_bidis);
//                    int bidis = -1;
//                    try {
//                        bidis = Integer.parseInt(strLine.substring(start_bidis, end_bidis));
//                    } catch (Exception e) {
//                    }
//
//                    final int start_lqi = strLine.indexOf("LQI ") + 4;
//                    final int end_lqi = strLine.indexOf(" ", start_lqi);
//                    int lqi = -1;
//                    try {
//                        lqi = Integer.parseInt(strLine.substring(start_lqi, end_lqi));
//                    } catch (Exception e) {
//                    }
//
//                    String node_id = "";
//                    if (strLine.contains("iSense::")) {
//                        node_id = strLine.substring(strLine.indexOf("iSense::") + 8, strLine.indexOf(" ", strLine.indexOf("iSense::")));
//                    } else if (strLine.contains("telosB::")) {
//                        node_id = strLine.substring(strLine.indexOf("telosB::") + 8, strLine.indexOf(" ", strLine.indexOf("telosB::")));
//                    }
//                    final int start_temp = strLine.indexOf("EM_T ") + 5;
//                    final int end_temp = strLine.indexOf(" ", start_temp);
//                    int temp = -200;
//                    try {
//                        temp = Integer.parseInt(strLine.substring(start_temp, end_temp));
//                    } catch (Exception e) {
//                    }
//
//                    final int start_lux = strLine.indexOf("EM_L ") + 5;
//                    final int end_lux = strLine.indexOf("\"", start_lux);
//                    int lux = -1;
//                    try {
//                        lux = Integer.parseInt(strLine.substring(start_lux, end_lux));
//                    } catch (Exception e) {
//                    }
//
//                    final int start_humid = strLine.indexOf("EM_H ") + 5;
//                    final int end_humid = strLine.indexOf(" ", start_humid);
//                    int humid = -1;
//                    try {
//                        humid = Integer.parseInt(strLine.substring(start_humid, end_humid));
//                    } catch (Exception e) {
//                    }
//
//                    final int start_inflight = strLine.indexOf("EM_I ") + 5;
//                    final int end_inflight = strLine.indexOf(" ", start_inflight);
//                    int inflight = -1;
//                    try {
//                        inflight = Integer.parseInt(strLine.substring(start_inflight, end_inflight));
//                    } catch (Exception e) {
//                    }


                // send_temp(node_id, temp, milis);
                // send_lux(node_id, lux, milis);
                // send_bidis(node_id, bidis, milis);
                // send_lqi(node_id, lqi, milis);

//
//                    if (temp != -200) {
//                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Temperature'," + temp + "," + milis + ")");
//                    }
//                    if ((lux != -1) && (lux < 10000)) {
//                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Light'," + lux + "," + milis + ")");
//                    } else {
//                        System.out.println("not sending wrong data for lux node " + node_id + " data is " + strLine.substring(start_lux, end_lux));
//                    }
//                    if (bidis > -1) {
//                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Neighbors'," + bidis + "," + milis + ")");
//                    }
//                    if (lqi > -1) {
//                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Lqi'," + lqi + "," + milis + ")");
//                    }
//                    if (humid > -1) {
//                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Humidity'," + humid + "," + milis + ")");
//                    }
//                    if (inflight > -1) {
//                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','Infrared'," + inflight + "," + milis + ")");
//                    }
//                    statement.executeBatch();                  "INSERT INTO link (id,nodeidA,nodeidB,quality,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','" + sender_id.substring(2) + "'," + value + "," + milis + ")"
//                    statement.clearBatch();
//
//                    System.out.println(node_id + " saved values to database");

//                } else if (strLine.contains("airquality::")) {
//
//                    String type = "";
//                    if (strLine.contains("SVal1:")) {
//                        type = "CO2";
//                    } else if (strLine.contains("SVal2:")) {
//                        type = "CO";
//                    } else if (strLine.contains("SVal3:")) {
//                        type = "CH4";
//                    }
//                    if (!type.equals("")) {
//                        final int start_value = strLine.indexOf("SVal") + 6;
//                        final int end_value = strLine.indexOf(" ", start_value);
//                        final int value = Integer.parseInt(strLine.substring(start_value, end_value));
//
//
//                        String node_id = "";
//                        if (strLine.contains("airquality::")) {
//                            node_id = strLine.substring(strLine.indexOf("airquality::") + 12, strLine.indexOf(" ", strLine.indexOf("airquality::")));
//                        }
//
//
//                        connectionURL = "jdbc:mysql://150.140.5.11:3306/dataCollector";
//                        connection = null;
//                        statement = null;
//
//                        // Load JBBC driver "com.mysql.jdbc.Driver".
//                        Class.forName("com.mysql.jdbc.Driver").newInstance();
//                        connection = DriverManager.getConnection(connectionURL, db_username, db_password);
//                        statement = connection.createStatement();
//
//                        final long milis = nowtime.getTime();
//
//                        statement.addBatch("INSERT INTO measurement (id,nodeid,measurementType,value,time) VALUES " + "(NULL,'" + node_id.substring(2) + "','" + type + "'," + value + "," + milis + ")");
//
//                        statement.executeBatch();
//                        statement.clearBatch();
//
//                        System.out.println(node_id + " saved values to database");
//
//                        //statement.close();
//                        //connection.close();
//
//
//                    }
//                }
            } catch (Exception e) {
                log.error("Node " + node_id + " - " + e.toString());
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
