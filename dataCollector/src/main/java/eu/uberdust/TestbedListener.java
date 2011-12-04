package eu.uberdust;

import eu.uberdust.controller.TestbedController;
import eu.uberdust.datacollector.DataCollector;
import eu.uberdust.nodeflasher.NodeFlasherController;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/3/11
 * Time: 2:57 PM
 */
public class TestbedListener {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TestbedListener.class);

    public static void main(final String[] args) {
        LOGGER.debug("starting TestbedController");
        TestbedController.getInstance();
        LOGGER.debug("starting NodeFlasherController");
        new NodeFlasherController();
        LOGGER.debug("starting DataCollector");
        final DataCollector dataCollector = new DataCollector();
        dataCollector.start();
        LOGGER.debug("up and running");
    }
}
