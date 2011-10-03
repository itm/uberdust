package eu.uberdust;

import eu.uberdust.datacollector.DataCollector;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/3/11
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestbedListener {

    public static void main(String[] args) {
        DataCollector dataCollector = new DataCollector();
        dataCollector.start();
    }
}
