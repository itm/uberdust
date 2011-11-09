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

    public static void main(final String[] args) {
        final DataCollector dataCollector = new DataCollector();
        dataCollector.start();
        TestbedController.getInstance();
        new NodeFlasherController();

    }
}
