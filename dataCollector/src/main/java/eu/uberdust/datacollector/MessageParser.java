package eu.uberdust.datacollector;

import eu.wisebed.wisedb.HibernateUtil;
import eu.wisebed.wisedb.controller.LinkReadingController;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MessageParser implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(DataCollector.class);

    private transient final String strLine;
    private transient final Map<String, String> sensors;

    /**
     * @param msg    the message received from the testbed
     * @param senses the Map containing the sensor codenames on testbed , capability names
     */
    public MessageParser(final String msg, final Map<String, String> senses) {

        strLine = msg.substring(msg.indexOf("binaryData:") + "binaryData:".length());
        sensors = senses;
    }


    /**
     * extracts the nodeid from a received testbed message
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
    public void run() {


        //get the node id
        final String node_id = extractNodeId(strLine);

        //if there is a node id
        if ("".equals(node_id)) {
            return;
        }

        LOGGER.debug("Node id is " + node_id);
        //check for capability readings
        boolean found_reading = false;
        //check for all given capabilities

        for (String sensor : sensors.keySet()) {
            if (strLine.indexOf(sensor) > 0) {
                found_reading = true;
                final int start = strLine.indexOf(sensor) + sensor.length() + 1;

                int end = strLine.indexOf(' ', start);
                if (end == -1) {
                    end = strLine.length() - 2;
                }
                int value;
                try {
                    value = Integer.parseInt(strLine.substring(start, end));
                    LOGGER.debug(sensors.get(sensor) + " value " + value + " node " + node_id);
                    commitNodeReading(node_id, sensors.get(sensor), value);

                } catch (Exception e) {
                    LOGGER.error("Cannot parse value for " + sensor + "'" + strLine.substring(start, end) + "'");
                }

                break;
            }
        }

        //if not a node reading message
        if (!found_reading) {
            // check for link down message
            if (strLine.contains("LINK_DOWN")) {
                //get the target id
                final int target_start = strLine.indexOf("LINK_DOWN") + "LINK_DOWN".length() + 1;
                final int target_end = strLine.indexOf(' ', target_start);

                commitLinkReading(node_id, strLine.substring(target_start, target_end), 0);

            } else if (strLine.contains("LINK_UP")) {
                //get the target id
                final int target_start = strLine.indexOf("LINK_UP") + "LINK_UP".length() + 1;
                final int target_end = strLine.indexOf(' ', target_start);

                commitLinkReading(node_id, strLine.substring(target_start, target_end), 1);
            }
        }
    }

    /**
     * commits a nodeReading to the database using the REST interface
     *
     * @param nodeId     the id of the node reporting the reading
     * @param capability the name of the capability
     * @param value      the value of the reading
     */
    private void commitNodeReading(final String nodeId, final String capability, final int value) {
        //get the node from hibernate
        final String testbedUrnPrefix = "urn:wisebed:ctitestbed:";
        final String testbedCapPrefix = "urn:wisebed:node:capability:";
        final String nodeUrn = testbedUrnPrefix + nodeId;
        final String capabilityName = testbedCapPrefix.toLowerCase() + capability.toLowerCase();
        final long milis = System.currentTimeMillis();

        final String insertReadingUrl = "http://gold.cti.gr:8080/uberdust/rest/testbed/1/node/" + nodeUrn + "/capability/" + capabilityName + "/insert/timestamp/" + milis + "/reading/" + value;

        HttpURLConnection httpURLConnection = null;
        try {
            final URL url = new URL(insertReadingUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                LOGGER.debug("Added " + nodeUrn + "," + capabilityName + "," + value);
            } else {
                LOGGER.error("Problem with " + nodeUrn + "," + capabilityName + "," + value + " Response: " + httpURLConnection.getResponseCode());
            }

        } catch (final MalformedURLException exception) {
            LOGGER.error(exception);
        } catch (final IOException exception) {
            LOGGER.error(exception);
        } finally {
            try {
                httpURLConnection.disconnect();
            } catch (NullPointerException ignore) {
            }
        }
    }

    /**
     * commits a nodeReading to the database using the Hibernate
     *
     * @param sourceId the id of the source node of the link
     * @param targetId the id of the target node of the link
     * @param status   the status value of the link
     */
    private void commitLinkReading(final String sourceId, final String targetId, final int status) {
        final String testbedUrnPrefix = "urn:wisebed:ctitestbed:";
        final String testbedCapPrefix = "status";
        final String sourceUrn = testbedUrnPrefix + sourceId;
        final String targetUrn = testbedUrnPrefix + targetId;

        LOGGER.debug("Fount a link down " + sourceUrn + "<<--" + status + "-->>" + targetUrn);

        final Transaction transaction = HibernateUtil.getInstance().getSession().beginTransaction();
        try {
            // insert reading
            LinkReadingController.getInstance().insertReading(sourceUrn, targetUrn, testbedCapPrefix, testbedUrnPrefix, status, 0,
                    new java.util.Date());
            transaction.commit();
            LOGGER.debug("Added Link " + sourceUrn + "<<--" + status + "-->>" + targetUrn);
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Problem Link " + sourceUrn + "<<--" + status + "-->>" + targetUrn);
        } finally {
            HibernateUtil.getInstance().closeSession();
        }
    }
}
