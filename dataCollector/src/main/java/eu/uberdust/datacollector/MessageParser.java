package eu.uberdust.datacollector;

import eu.wisebed.wisedb.HibernateUtil;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import java.util.Map;

public class MessageParser implements Runnable {

    private final String strLine;
    private final Map<String, String> sensors;

    public MessageParser(final String msg, final Map<String, String> senses) {

        strLine = msg.substring(msg.indexOf("binaryData:") + "binaryData:".length());
        sensors = senses;
    }

    private static final Logger LOGGER = Logger.getLogger(DataCollector.class);


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


    private void commitNodeReading(final String node_id, final String sensor, final int value) {
        //get the node from hibernate
        final String testbedUrnPrefix = "urn:wisebed:ctitestbed:";
        final String testbedCapPrefix = "urn:wisebed:node:capability:";
        final String nodeId = testbedUrnPrefix + node_id;
        final String capabilityName = testbedCapPrefix .toLowerCase() + sensor.toLowerCase();


        final Transaction transaction = HibernateUtil.getInstance().getSession().beginTransaction();
        try {
            // insert reading
            NodeReadingController.getInstance().insertReading(nodeId, capabilityName, testbedUrnPrefix,
                    value, new java.util.Date());
            transaction.commit();
            LOGGER.info("Added " + nodeId + "," + capabilityName + "," + value);

        } catch (Exception e) {
            LOGGER.error("Problem with " + nodeId + "," + capabilityName + "," + value + " Exception: ");
            LOGGER.error(e);
            transaction.rollback();
        } finally {
            HibernateUtil.getInstance().closeSession();
        }
    }

    private void commitLinkReading(final String sId, final String tId,final int status) {
        final String testbedUrnPrefix = "urn:wisebed:ctitestbed:";
        final String testbedCapPrefix = "status";
        final String sourceId = testbedUrnPrefix + sId;
        final String targetId = testbedUrnPrefix + tId;

        LOGGER.debug("Fount a link down " + sourceId + "<<--" + status + "-->>" + targetId);

        final Transaction transaction = HibernateUtil.getInstance().getSession().beginTransaction();
        try {
            // insert reading
            LinkReadingController.getInstance().insertReading(sourceId, targetId, testbedCapPrefix, testbedUrnPrefix, status, 0,
                    new java.util.Date());
            transaction.commit();
            LOGGER.info("Added Link " + sourceId + "<<--" + status + "-->>" + targetId);
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("Problem Link " + sourceId + "<<--" + status + "-->>" + targetId);
        } finally {
            HibernateUtil.getInstance().closeSession();
        }
    }
}
