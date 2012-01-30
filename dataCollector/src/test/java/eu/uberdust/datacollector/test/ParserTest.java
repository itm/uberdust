//package eu.uberdust.datacollector.test;
//
//import eu.uberdust.datacollector.parsers.MessageParser;
//import eu.uberdust.reading.LinkReading;
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Unit test for simple App.
// */
//public class ParserTest
//        extends TestCase {
//
//    /**
//     * Application property file name.
//     */
//    private static final String PROPERTY_FILE = "src/test/resources/dataCollector.properties";
//    /**
//     * map of the names used in iSense application to capability names.
//     */
//    private final transient Map<String, String> sensors = new HashMap<String, String>();
//    private static final Logger LOGGER = Logger.getLogger(ParserTest.class);
//
//
//    /**
//     * Create the test case
//     *
//     * @param testName name of the test case
//     */
//    public ParserTest(String testName) {
//        super(testName);
//    }
//
//    /**
//     * @return the suite of tests being tested
//     */
//    public static Test suite() {
//        return new TestSuite(ParserTest.class);
//    }
//
//    /**
//     * Rigourous Test :-)
//     */
//    public void testApp() {
//
//        String testString = "0x7f|0x69|0x70|0x1|0x1|0x1|0x1|0x3|0x2|0x4|0x4|0x6|0x4|0x1|0x9|0x6|0x6|0x8|0x5|";
//        System.out.println(testString.replaceAll("0x", ""));
//        String testbedPrefix = "urn:wisebed:ctitestbed:";
//
//        System.out.println(testString.replace('|', ','));
//
//
//        BasicConfigurator.configure();
//        LOGGER.setLevel(Level.ALL);
//
//        LinkReading linkReading = new LinkReading();
//        linkReading.setTestbedId("1");
//        linkReading.setLinkSource("urn:qopbot:kandalf");
//        linkReading.setLinkTarget("urn:qopbot:destiny");
//        linkReading.setCapabilityName("rtt");
//        linkReading.setTimestamp("1111");
//        linkReading.setReading("21");
//        LOGGER.info(linkReading.toRestString());
//
//        final String[] sensorsNames = "temperature,humidity,ir,co2,co,ch4,light,batterycharge,barometricpressure,light1,light2,light3,light4,pir,pressure".split(",");
//        final String[] sensorsPrefixes = "EM_T,EM_H,EM_I,SVal1:,SVal2:,SVal3:,EM_L,BA_C,EM_P,RL1,RL2,RL3,RL4,EM_E,CS".split(",");
//
//        for (int i = 0; i < sensorsNames.length; i++) {
//            sensors.put(sensorsPrefixes[i], sensorsNames[i]);
//        }
//        long millis = System.currentTimeMillis();
//        String eventString = "binaryData:h\\000id::0x1ccd EM_E 0 ";
//        MessageParser messageParser = new MessageParser(eventString, sensors, testbedPrefix, 1);
//        messageParser.parse();
//        LOGGER.debug("Parsing and thread creation takes " + (System.currentTimeMillis() - millis) + " millis");
//
//        millis = System.currentTimeMillis();
//        eventString = "binaryData:h\\000id::0x99c EM_L 165 ";
//        messageParser = new MessageParser(eventString, sensors, testbedPrefix, 1);
//        messageParser.parse();
//        LOGGER.debug("Parsing needs " + (System.currentTimeMillis() - millis) + " millis");
//
//
//        millis = System.currentTimeMillis();
//        eventString = "binaryData:h\\000id::0x99c RL4 0 ";
//        messageParser = new MessageParser(eventString, sensors, testbedPrefix, 1);
//        messageParser.parse();
//        LOGGER.debug("Parsing needs " + (System.currentTimeMillis() - millis) + " millis");
//
//        assertTrue(true);
//    }
//}
//
