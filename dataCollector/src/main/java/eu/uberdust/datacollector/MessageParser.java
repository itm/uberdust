package eu.uberdust.datacollector;

import eu.wisebed.wisedb.HibernateUtil;
import eu.wisebed.wisedb.controller.LinkReadingController;
import eu.wisebed.wisedb.controller.NodeReadingController;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/29/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageParser implements Runnable {

    private String toString;
    private static String[] Sensors_names, Sensors_prefixes;

    public MessageParser(String msg, String[] Sensors_names_, String[] Sensors_prefixes_) {
        toString = msg;
        Sensors_names = Sensors_names_;
        Sensors_prefixes = Sensors_prefixes_;

    }

    private static Logger log = Logger.getLogger(DataCollector.class);

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
        //Get only the text part
        final String strLine = toString.substring(toString.indexOf("binaryData:") + "binaryData:".length());
//        log.debug("Received " + strLine);

        //get the node id
        final String node_id = extractNodeId(strLine);

        try {
            //if there is a node id
            if (node_id != "") {
                log.debug("Node id is " + node_id);
                //check for capability readings
                boolean found_reading = false;
                //check for all given capabilitirs
                for (int i = 0; i < Sensors_prefixes.length; i++) {
                    if (strLine.indexOf(Sensors_prefixes[i]) > 0) {
                        found_reading = true;
                        final int start = strLine.indexOf(Sensors_prefixes[i]) + Sensors_prefixes[i].length() + 1;

                        int end = strLine.indexOf(" ", start);
                        if (end == -1) {
                            end = strLine.length() - 2;
                        }
                        int value = -1;
                        try {
                            value = Integer.parseInt(strLine.substring(start, end));
                            log.debug(Sensors_names[i] + " value " + value + " node " + node_id);
                            //check if inside accepted values
                            if (value > -1) {
                                // Initialize hibernate
                                HibernateUtil.connectEntityManagers();

                                Transaction tx = HibernateUtil.getInstance().getSession().beginTransaction();
                                log.debug("value exists");
                                //get the node from hibernate
                                final String nodeId = "urn:wisebed:ctitestbed:" + node_id;
                                final String capabilityName = "urn:wisebed:node:capability:" + Sensors_names[i];
                                final double readingValue = value;

                                try {
                                    // insert reading
                                    NodeReadingController.getInstance().insertReading(nodeId, capabilityName, readingValue, new java.util.Date());
                                    tx.commit();
                                    log.info("Added " + nodeId + "," + capabilityName + "," + readingValue);

                                } catch (Exception e) {
                                    log.error("Problem with " + nodeId + "," + capabilityName + "," + readingValue);
                                    tx.rollback();
                                } finally {
                                    HibernateUtil.getInstance().closeSession();
                                }
                                break;
                            } else {
                                log.error("error in value -1");
                            }
                        } catch (Exception e) {
                            log.error("Cannot parse value for " + Sensors_prefixes[i] + "'" + strLine.substring(start, end) + "'");
                        }
                    }
                }

                //if not a node reading message
                if (!found_reading) {
                    // check for link down message
                    if (strLine.contains("LINK_DOWN")) {
                        // Initialize hibernate
                        HibernateUtil.connectEntityManagers();
                        Transaction tx = HibernateUtil.getInstance().getSession().beginTransaction();
                        //get the target id
                        final int target_start = strLine.indexOf("LINK_DOWN") + "LINK_DOWN".length() + 1;
                        final int target_end = strLine.indexOf(" ", target_start);

                        final String sourceId = "urn:wisebed:ctitestbed:" + node_id;
                        final String targetId = "urn:wisebed:ctitestbed:" + strLine.substring(target_start, target_end);


                        log.debug("Fount a link down " + sourceId + "<<--X--->>" + targetId);

                        try {
                            // insert reading
                            LinkReadingController.getInstance().insertReading(sourceId, targetId, "status", 0, new Date());
                            tx.commit();
                            log.info("Added Link " + sourceId + "<<--X-->>" + targetId);
                        } catch (Exception e) {
                            tx.rollback();
                            log.error("Problem Link " + sourceId + "<<--X-->>" + targetId);
                        } finally {
                            HibernateUtil.getInstance().closeSession();
                        }

                    } else if (strLine.contains("LINK_UP")) {
                        // Initialize hibernate
                        HibernateUtil.connectEntityManagers();
                        Transaction tx = HibernateUtil.getInstance().getSession().beginTransaction();
                        //get the target id
                        final int target_start = strLine.indexOf("LINK_UP") + "LINK_UP".length() + 1;
                        final int target_end = strLine.indexOf(" ", target_start);

                        final String sourceId = "urn:wisebed:ctitestbed:" + node_id;
                        final String targetId = "urn:wisebed:ctitestbed:" + strLine.substring(target_start, target_end);

                        log.debug("Fount a link up " + sourceId + "<<------>>" + targetId);

                        try {
                            // insert reading
                            LinkReadingController.getInstance().insertReading(sourceId, targetId, "status", 1, new Date());
                            tx.commit();
                            log.info("Added Link " + sourceId + "<<----->>" + targetId);
                        } catch (Exception e) {
                            tx.rollback();
                            log.error("Problem Link " + sourceId + "<<----->>" + targetId);
                        } finally {
                            HibernateUtil.getInstance().closeSession();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Node " + node_id + " - " + e.toString());
        }

    }
}
