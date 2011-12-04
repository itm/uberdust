package eu.uberdust.datacollector.parsers;

import eu.uberdust.datacollector.DataCollector;
import eu.uberdust.reading.LinkReading;
import eu.uberdust.reading.NodeReading;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

/**
 * Parses a message received and adds data to a wisedb database.
 */
public class RestMessageParser implements Runnable { //NOPMD

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
    private static final String TESTBED_SERVER = "http://uberdust.cti.gr/rest";


    /**
     * @param msg    the message received from the testbed
     * @param senses the Map containing the sensor codenames on testbed , capability names
     */
    public RestMessageParser(final String msg, final Map<String, String> senses) {
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
                    String milliseconds = String.valueOf(System.currentTimeMillis());
                    if (nodeId.contains("1ccd")) {
                        if (sensors.get(sensor).equals("pir")) {
                            milliseconds = strLine.split(" ")[4];
                            LOGGER.info("setting event time to " + milliseconds + " message '" + strLine + "'");
                        }
                    }

                    commitNodeReading(nodeId, sensors.get(sensor), value, milliseconds);


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
     * @param nodeId       the id of the node reporting the reading
     * @param capability   the name of the capability
     * @param value        the value of the reading
     * @param milliseconds
     */
    private void commitNodeReading(final String nodeId, final String capability, final int value, String milliseconds) {

        final String nodeUrn = TESTBED_URN + nodeId;
        final String capabilityName = (CAPABILITY_PREFIX + capability).toLowerCase(Locale.US);


        final NodeReading nodeReading = new NodeReading();
        nodeReading.setTestbedId(TESTBED_ID);
        nodeReading.setNodeId(nodeUrn);
        nodeReading.setCapabilityName(capabilityName);
        nodeReading.setTimestamp(milliseconds);
        nodeReading.setReading(String.valueOf(value));

        final StringBuilder urlBuilder = new StringBuilder(TESTBED_SERVER);
        urlBuilder.append(nodeReading.toRestString());
        final String insertReadingUrl = urlBuilder.toString();

        callUrl(insertReadingUrl);
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
        linkReading.setTimestamp(String.valueOf(milliseconds));
        linkReading.setReading(String.valueOf(status));

        final StringBuilder urlBuilder = new StringBuilder(TESTBED_SERVER);
        urlBuilder.append(linkReading.toRestString());
        final String insertReadingUrl = urlBuilder.toString();

        callUrl(insertReadingUrl);
    }

    /**
     * Opens a connection over the Rest Interfaces to the server and adds the event.
     *
     * @param urlString the string url that describes the event
     */
    private void callUrl(final String urlString) {
        HttpURLConnection httpURLConnection = null;

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            LOGGER.error(e);
            return;
        }

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                LOGGER.debug("Added " + urlString);
            } else {
                final StringBuilder errorBuilder = new StringBuilder("Problem ");
                errorBuilder.append("with ").append(urlString);
                errorBuilder.append(" Response: ").append(httpURLConnection.getResponseCode());
                LOGGER.error(errorBuilder.toString());
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            LOGGER.error(e);
        }


    }

}
