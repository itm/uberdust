package eu.uberdust.datacollector.parsers;

import eu.uberdust.datacollector.DataCollector;
import eu.uberdust.reading.LinkReading;
import eu.uberdust.reading.NodeReading;
import eu.uberdust.uberlogger.UberLogger;
import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.Map;

/**
 * Parses a message received and adds data to a wisedb database.
 */
public class MessageParser implements Runnable { //NOPMD

    /**
     * LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(DataCollector.class);


    /**
     * Text line of the message received.
     */
    private final transient String strLine;
    /**
     * Map of all the codeNames-capabilities.
     */
    private final transient Map<String, String> sensors;
    /**
     * ID of the testbed Monitored.
     */
    private static final String TESTBED_ID = "1";
    /**
     * URN of the testbed Monitored.
     */
    private static final String TESTBED_URN = "urn:wisebed:ctitestbed:";
    /**
     * Testbed Capability prefix.
     */
    private static final String CAPABILITY_PREFIX = "urn:wisebed:node:capability:";
    /**
     * Position of the timestamp in a node Reading .
     */
    private static final int TIMESTAMP_POS = 4;


    /**
     * @param msg    the message received from the testbed
     * @param senses the Map containing the sensor codenames on testbed , capability names
     */
    public MessageParser(final String msg, final Map<String, String> senses) {
        strLine = msg.substring(msg.indexOf("binaryData:") + "binaryData:".length());
        sensors = senses;
    }


    /**
     * extracts the nodeid from a received testbed message.
     *
     * @param paramLine the message received from the testbed
     * @return the node id in hex
     */
    private String extractNodeId(final String paramLine) {
        final String line = paramLine.substring(7);
        final int start = line.indexOf("0x");
        if (start > 0) {
            final int end = line.indexOf(' ', start);
            if (end > 0) {
                return line.substring(start, end);
            }
        }
        return "";
    }

    /**
     * Starts the parser thread.
     */
    public final void run() {
        parse();
    }


    /**
     * Parsers the message and create the event to report.
     */
    public final void parse() {

        LOGGER.debug(strLine);


        if (strLine.contains("id::0x1ccd EM_E 1")) {
            String milliseconds = strLine.split(" ")[TIMESTAMP_POS];
            UberLogger.getInstance().LOG(milliseconds, "Τ21");
        }

        //get the node id
        final String nodeId = extractNodeId(strLine);

        //if there is a node id
        if (!"".equals(nodeId)) {

            LOGGER.debug("Node id is " + nodeId);

            //check for Link Readings
            if (!checkLinkReading(nodeId)) {
                //check for all given capabilities
                for (String sensor : sensors.keySet()) {
                    if (checkSensor(sensor, nodeId)) {
                        break;
                    }

                }
            }
        }

    }

    /**
     * checks for the a node reading.
     *
     * @param sensor the sensor to check for
     * @param nodeId the id of the reporting node
     * @return true if contains a NodeReading
     */
    private boolean checkSensor(final String sensor, final String nodeId) {
        boolean retVal = false;
        if (strLine.contains(sensor)) {
            retVal = true;
            final int start = strLine.indexOf(sensor) + sensor.length() + 1;
            int end = strLine.indexOf(' ', start);
            if (end == -1) {
                end = strLine.length() - 2;
            }
            try {
                final int value = Integer.parseInt(strLine.substring(start, end));
                LOGGER.debug(sensors.get(sensor) + " value " + value + " node " + nodeId);
                String milliseconds = String.valueOf(System.currentTimeMillis());


//                if ((nodeId.contains("1ccd")) && (sensor.contains("EM_E"))) {
//                    milliseconds = strLine.split(" ")[TIMESTAMP_POS];
//                    LOGGER.info("setting eventt to " + milliseconds);
//
//                }
                commitNodeReading(nodeId, sensors.get(sensor), value, milliseconds);
            } catch (Exception e) {
                LOGGER.error("Parse Error" + sensor + "'" + strLine.substring(start, end) + "'");
            }
        }
        return retVal;
    }

    /**
     * Checks the message received for a new node reading.
     *
     * @param nodeId nodeId the id of the node reporting the reading
     * @return true of a reading was found
     */
    private boolean checkLinkReading(final String nodeId) {
        if (strLine.contains("LINK_DOWN")) {
            //get the target id
            final int start = strLine.indexOf("LINK_DOWN") + "LINK_DOWN".length() + 1;
            final int end = strLine.indexOf(' ', start);
            commitLinkReading(nodeId, strLine.substring(start, end), "status", 0);

        } else if (strLine.contains("LINK_UP")) {
            //get the target id
            final int start = strLine.indexOf("LINK_UP") + "LINK_UP".length() + 1;
            final int end = strLine.indexOf(' ', start);
            commitLinkReading(nodeId, strLine.substring(start, end), "status", 1);
        } else if (strLine.contains("command=")) {
            LOGGER.info(strLine);
            final int start = strLine.indexOf("dest::") + "dest::".length();
            final int end = strLine.indexOf(" ", start);
            LOGGER.info("nodid:" + strLine.substring(start, end));

            final int commandStart = strLine.indexOf("command=");
            final int commandStop = strLine.indexOf(" ", commandStart);
            LOGGER.info("commandString:" + strLine.substring(commandStart, commandStop));
            final String[] commandString = strLine.substring(commandStart, commandStop).split("0x");

            int commandTotal = 0;
            //LOGGER.info("args " + commandString.length + " cont " + commandString);
            try {
                LOGGER.info(commandString[4].substring(0, commandString[4].indexOf('|')));
                commandTotal += 100 * Integer.parseInt(commandString[4].substring(0, commandString[4].indexOf('|')), 16);
                LOGGER.info(commandString[5].substring(0, commandString[5].indexOf('|')));
                commandTotal += 10 * Integer.parseInt(commandString[5].substring(0, commandString[5].indexOf('|')), 16);
                LOGGER.info(commandString[6].substring(0, commandString[6].indexOf('|')));
                commandTotal += Integer.parseInt(commandString[6].substring(0, commandString[6].indexOf('|')), 16);
                commitLinkReading(nodeId, strLine.substring(start, end), "command", commandTotal);
                LOGGER.info("COMMAND " + nodeId + " " + strLine.substring(start, end) + " " + commandTotal);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Commits a nodeReading to the database using the REST interface.
     *
     * @param nodeId     the id of the node reporting the reading
     * @param capability the name of the capability
     * @param value      the value of the reading
     * @param msec       timestamp in milliseconds
     */
    private void commitNodeReading(final String nodeId, final String capability, final int value, final String msec) {

        final String nodeUrn = TESTBED_URN + nodeId;
        final String capabilityName = (CAPABILITY_PREFIX + capability).toLowerCase(Locale.US);


        final NodeReading nodeReading = new NodeReading();
        nodeReading.setTestbedId(TESTBED_ID);
        nodeReading.setNodeId(nodeUrn);
        nodeReading.setCapabilityName(capabilityName);
        nodeReading.setTimestamp(msec);
        nodeReading.setReading(String.valueOf(value));

        new WsCommiter(nodeReading);
    }

    /**
     * commits a nodeReading to the database using the Hibernate.
     *
     * @param source the id of the source node of the link
     * @param target the id of the target node of the link
     * @param value  the status value of the link
     */
    private void commitLinkReading(final String source, final String target, final String testbedCap, final int value) {
        final String sourceUrn = TESTBED_URN + source;
        final String targetUrn = TESTBED_URN + target;

        LOGGER.debug("LinkReading" + sourceUrn + "<->" + targetUrn + " " + testbedCap + " " + value);
        final long milliseconds = System.currentTimeMillis();
        final LinkReading linkReading = new LinkReading();
        linkReading.setTestbedId(TESTBED_ID);
        linkReading.setLinkSource(sourceUrn);
        linkReading.setLinkTarget(targetUrn);
        linkReading.setCapabilityName(testbedCap);
        linkReading.setTimestamp(String.valueOf(milliseconds));
        linkReading.setReading(String.valueOf(value));


        new RestCommiter(linkReading);
    }
}
