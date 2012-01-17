package eu.uberdust;

import eu.uberdust.controller.TestbedController;
import eu.uberdust.datacollector.DataCollector;
import eu.uberdust.nodeflasher.NodeFlasherController;
import eu.uberdust.util.PropertyReader;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/3/11
 * Time: 2:57 PM
 */
public class TestbedListener {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TestbedListener.class);

    public static void main(final String[] args) {

        if (PropertyReader.getInstance().getProperties().get("use.controller").equals("1")) {
            LOGGER.info("starting TestbedController");
            TestbedController.getInstance();
        }
        if (PropertyReader.getInstance().getProperties().get("use.nodeflasher").equals("1")) {
            LOGGER.info("starting NodeFlasherController");
            new NodeFlasherController();
        }
        if (PropertyReader.getInstance().getProperties().get("use.datacollector").equals("1")) {
            LOGGER.info("starting DataCollector");
            final Thread dataCollector = new Thread(new DataCollector());
            dataCollector.run();
        }
        LOGGER.info("up and running");
    }
}
