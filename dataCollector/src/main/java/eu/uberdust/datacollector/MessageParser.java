package eu.uberdust.datacollector;

import eu.uberdust.communication.websocket.InsertReadingWebSocketClient;
import eu.uberdust.eu.uberdust.reading.LinkReading;
import eu.uberdust.eu.uberdust.reading.NodeReading;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Locale;
import java.util.Map;

/**
 * Parses a message received and adds data to a wisedb database.
 */
public class MessageParser implements Runnable {                   // NOPMD

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
     * ID of the testbed Monitored
     */
    private static final String TESTBED_ID = "1";
    private static final String TESTBED_URN = "urn:wisebed:ctitestbed:";
    private static final String CAPABILITY_PREFIX = "urn:wisebed:node:capability:";


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
     *
     */
    public final void run() {
        parse();
    }


    public final void parse() {

        LOGGER.debug(strLine);
        //get the node id
        final String nodeId = extractNodeId(strLine);

        //if there is a node id
        if ("".equals(nodeId)) {
            LOGGER.debug("no node id");
            return;
        }

        LOGGER.debug("Node id is " + nodeId);
        //check for capability readings
        boolean foundReading = false;
        //check for all given capabilities

        for (String sensor : sensors.keySet()) {
            if (strLine.indexOf(sensor) > 0) {
                foundReading = true;
                final int start = strLine.indexOf(sensor) + sensor.length() + 1;

                int end = strLine.indexOf(' ', start);
                if (end == -1) {
                    end = strLine.length() - 2;
                }
                int value;
                try {
                    value = Integer.parseInt(strLine.substring(start, end));
                    LOGGER.debug(sensors.get(sensor) + " value " + value + " node " + nodeId);
                    commitNodeReading(nodeId, sensors.get(sensor), value);

                } catch (Exception e) {
                    LOGGER.error("Cannot parse value for " + sensor + "'" + strLine.substring(start, end) + "'");
                }

                break;
            }
        }

        //if not a node reading message
        if (!foundReading) {
            // check for link down message
            if (strLine.contains("LINK_DOWN")) {
                //get the target id
                final int targetStart = strLine.indexOf("LINK_DOWN") + "LINK_DOWN".length() + 1;
                final int targetEnd = strLine.indexOf(' ', targetStart);
                commitLinkReading(nodeId, strLine.substring(targetStart, targetEnd), 0);

            } else if (strLine.contains("LINK_UP")) {
                //get the target id
                final int targetStart = strLine.indexOf("LINK_UP") + "LINK_UP".length() + 1;
                final int targetEnd = strLine.indexOf(' ', targetStart);
                commitLinkReading(nodeId, strLine.substring(targetStart, targetEnd), 1);
            }
        }
    }

    /**
     * Commits a nodeReading to the database using the REST interface.
     *
     * @param nodeId     the id of the node reporting the reading
     * @param capability the name of the capability
     * @param value      the value of the reading
     */
    private void commitNodeReading(final String nodeId, final String capability, final int value) {

        final String nodeUrn = TESTBED_URN + nodeId;
        final String capabilityName = (CAPABILITY_PREFIX + capability).toLowerCase(Locale.US);
        final long milliseconds = System.currentTimeMillis();

        final NodeReading nodeReading = new NodeReading();
        nodeReading.setTestbedId(TESTBED_ID);
        nodeReading.setNodeId(nodeUrn);
        nodeReading.setCapabilityName(capabilityName);
        nodeReading.setReading(String.valueOf(value));
        nodeReading.setTimestamp(String.valueOf(milliseconds));
        LOGGER.debug(nodeReading);

        try {
            LOGGER.info("adding " + nodeReading);
            InsertReadingWebSocketClient.getInstance().sendNodeReading(nodeReading);
            LOGGER.info("added " + nodeReading);
        } catch (Exception e) {
            LOGGER.error("InsertReadingWebSocketClient -node-" + e);
        }
    }

    /**
     * commits a nodeReading to the database using the Hibernate.
     *
     * @param sourceId the id of the source node of the link
     * @param targetId the id of the target node of the link
     * @param status   the status value of the link
     */
    private void commitLinkReading(final String sourceId, final String targetId, final int status) {
        final String testbedCap = "status";
        final String sourceUrn = TESTBED_URN + sourceId;
        final String targetUrn = TESTBED_URN + targetId;

        LOGGER.debug("Fount a link down " + sourceUrn + "<<--" + status + "-->>" + targetUrn);
        final long milliseconds = System.currentTimeMillis();

        final LinkReading linkReading = new LinkReading();
        linkReading.setTestbedId(TESTBED_ID);
        linkReading.setLinkSource(sourceUrn);
        linkReading.setLinkTarget(targetUrn);
        linkReading.setCapabilityName(testbedCap);
        linkReading.setReading(String.valueOf(status));
        linkReading.setTimestamp(String.valueOf(milliseconds));
        LOGGER.debug(linkReading.toString());

        try {
            LOGGER.info("adding " + linkReading);
            InsertReadingWebSocketClient.getInstance().setLinkReading(linkReading);
            LOGGER.info("added " + linkReading);
        } catch (Exception e) {
            LOGGER.error("InsertReadingWebSocketClient -link- " + e);
        }
    }

    /**
     * Sets the logging level
     *
     * @param level the desired loggin level
     */
    public void setLevel(final Level level) {
        LOGGER.setLevel(level);
    }
}
