package eu.uberdust.datacollector.test;

import eu.uberdust.datacollector.parsers.WsMessageParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class ParserTest
        extends TestCase {

    /**
     * Application property file name.
     */
    private static final String PROPERTY_FILE = "src/test/resources/dataCollector.properties";
    /**
     * map of the names used in iSense application to capability names.
     */
    private final transient Map<String, String> sensors = new HashMap<String, String>();
    private static final Logger LOGGER = Logger.getLogger(ParserTest.class);


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ParserTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        BasicConfigurator.configure();
        LOGGER.setLevel(Level.ALL);

        final String[] sensorsNames = "temperature,humidity,ir,co2,co,ch4,light,batterycharge,barometricpressure,light1,light2,light3,light4,pir,pressure".split(",");
        final String[] sensorsPrefixes = "EM_T,EM_H,EM_I,SVal1:,SVal2:,SVal3:,EM_L,BA_C,EM_P,RL1,RL2,RL3,RL4,EM_E,CS".split(",");

        for (int i = 0; i < sensorsNames.length; i++) {
            sensors.put(sensorsPrefixes[i], sensorsNames[i]);
        }
        long millis = System.currentTimeMillis();
        String eventString = "binaryData:h\\000id::0x1ccd EM_E 0 ";
        WsMessageParser messageParser = new WsMessageParser(eventString, sensors);
        messageParser.setLevel(Level.ALL);
        messageParser.parse();
        LOGGER.debug("Parsing and thread creation takes " + (System.currentTimeMillis() - millis) + " millis");

        millis = System.currentTimeMillis();
        eventString = "binaryData:h\\000id::0x99c EM_L 165 ";
        messageParser = new WsMessageParser(eventString, sensors);
        messageParser.parse();
        LOGGER.debug("Parsing needs " + (System.currentTimeMillis() - millis) + " millis");


        millis = System.currentTimeMillis();
        eventString = "binaryData:h\\000id::0x99c RL4 0 ";
        messageParser = new WsMessageParser(eventString, sensors);
        messageParser.parse();
        LOGGER.debug("Parsing needs " + (System.currentTimeMillis() - millis) + " millis");

        assertTrue(true);
    }
}

