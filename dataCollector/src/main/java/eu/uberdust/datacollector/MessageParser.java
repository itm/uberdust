package eu.uberdust.datacollector;

import eu.wisebed.wisedb.HibernateUtil;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.Map;

public class MessageParser implements Runnable {

    private final String strLine;
    private static Map<String, String> sensors = new HashMap<String, String>();

    public MessageParser(String msg, Map<String, String> senses) {

        strLine = msg.substring(msg.indexOf("binaryData:") + "binaryData:".length());
        sensors = senses;
    }

    private static final Logger log = Logger.getLogger(DataCollector.class);

    //USEd to get the node id from the string received
    private String extractNodeId(String linea) {
        final String line = linea.substring(7);
        final int start = line.indexOf("0x");
        if (start > 0) {
            final int end = line.indexOf(" ", start);
            if (end > 0) {
                return line.substring(start, end);
            }
        }
        return "";
    }

    public void run() {

        // Initialize hibernate
        HibernateUtil.connectEntityManagers();

        //get the node id
        final String node_id = extractNodeId(strLine);

        //if there is a node id
        if (node_id.equals("")) {
            return;
        }

        log.debug("Node id is " + node_id);
        //check for capability readings
        boolean found_reading = false;
        //check for all given capabilities

        for (String sensor : sensors.keySet()) {
            if (strLine.indexOf(sensor) > 0) {
                found_reading = true;
                final int start = strLine.indexOf(sensor) + sensor.length() + 1;

                int end = strLine.indexOf(" ", start);
                if (end == -1) {
                    end = strLine.length() - 2;
                }
                int value;
                try {
                    value = Integer.parseInt(strLine.substring(start, end));
                    log.debug(sensors.get(sensor) + " value " + value + " node " + node_id);
                    found_reading = true;

                    CommitNodeReading(node_id, sensors.get(sensor), value);

                } catch (Exception e) {
                    log.error("Cannot parse value for " + sensor + "'" + strLine.substring(start, end) + "'");
                }

                if (found_reading) break;
            }
        }

        //if not a node reading message
        if (!found_reading) {
            // check for link down message
            if (strLine.contains("LINK_DOWN")) {
                //get the target id
                final int target_start = strLine.indexOf("LINK_DOWN") + "LINK_DOWN".length() + 1;
                final int target_end = strLine.indexOf(" ", target_start);

                CommitLinkReading(node_id, strLine.substring(target_start, target_end), 0);

            } else if (strLine.contains("LINK_UP")) {
                //get the target id
                final int target_start = strLine.indexOf("LINK_UP") + "LINK_UP".length() + 1;
                final int target_end = strLine.indexOf(" ", target_start);

                CommitLinkReading(node_id, strLine.substring(target_start, target_end), 1);
            }
        }
    }


    private void CommitNodeReading(String node_id, String sensor, int value) {
        //get the node from hibernate
        final String testbedUrnPrefix = "urn:wisebed:ctitestbed:";
        final String testbedCapabilityPrefix = "urn:wisebed:node:capability:";
        final String nodeId = testbedUrnPrefix + node_id;
        final String capabilityName = testbedCapabilityPrefix + sensor;

        Transaction tx = HibernateUtil.getInstance().getSession().beginTransaction();
        try {
            // insert reading
            NodeReadingController.getInstance().insertReading(nodeId, capabilityName, testbedUrnPrefix,
                    value, new java.util.Date());
            tx.commit();
            log.info("Added " + nodeId + "," + capabilityName + "," + value);

        } catch (Exception e) {
            log.error("Problem with " + nodeId + "," + capabilityName + "," + value);
            tx.rollback();
        } finally {
            HibernateUtil.getInstance().closeSession();
        }
    }

    private void CommitLinkReading(String sId, String tId, int status) {
        final String testbedUrnPrefix = "urn:wisebed:ctitestbed:";
        final String testbedCapabilityPrefix = "urn:wisebed:link:capability:";
        final String sourceId = testbedUrnPrefix + sId;
        final String targetId = testbedCapabilityPrefix + tId;

        log.debug("Fount a link down " + sourceId + "<<--" + status + "-->>" + targetId);

        Transaction tx = HibernateUtil.getInstance().getSession().beginTransaction();
        try {
            // insert reading
            LinkReadingController.getInstance().insertReading(sourceId, targetId, "status", testbedUrnPrefix, status, 0,
                    new java.util.Date());
            tx.commit();
            log.info("Added Link " + sourceId + "<<--" + status + "-->>" + targetId);
        } catch (Exception e) {
            tx.rollback();
            log.error("Problem Link " + sourceId + "<<--" + status + "-->>" + targetId);
        } finally {
            HibernateUtil.getInstance().closeSession();
        }
    }
}
