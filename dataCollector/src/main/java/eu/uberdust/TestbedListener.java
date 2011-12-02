package eu.uberdust;

import eu.uberdust.controller.TestbedController;
import eu.uberdust.datacollector.DataCollector;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/3/11
 * Time: 2:57 PM
 */
public class TestbedListener {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TestbedListener.class);

    public static void main(final String[] args) {
        TestbedController.getInstance();
//        new NodeFlasherController();
        final DataCollector dataCollector = new DataCollector();
        dataCollector.start();
    }
}
