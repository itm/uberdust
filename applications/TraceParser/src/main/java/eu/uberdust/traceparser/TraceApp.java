package eu.uberdust.traceparser;

import eu.uberdust.traceparser.parsers.IntervalDurationParser;
import eu.uberdust.traceparser.parsers.TRParser;
import eu.uberdust.traceparser.parsers.UberParser;
import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 12/5/11
 * Time: 7:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class TraceApp {

    /**
     * Static Logger,
     */
    private static final Logger LOGGER = Logger.getLogger(TraceApp.class);


    /**
     * Application property file name.
     */
    private static final String PROPERTY_FILE = "parser.properties";

    private String[] runtimeLogFiles;

    private String[] uberLogFiles;
    private String threadLogFile;

    public TraceApp() {
        readProperties();
        final TRParser trParser = new TRParser(runtimeLogFiles);
        ArrayList<TrNodeReading> readings = trParser.returnReadings();

        readings = (new UberParser(uberLogFiles, readings)).returnReadings();

        final ArrayList<TrNodeReading> finalReadings = new ArrayList<TrNodeReading>();

        for (TrNodeReading reading : readings) {
            if (reading.isComplete()) {
                finalReadings.add(reading);
            }
        }


//        new ThreadParser(threadLogFile);
//        new TotalTimeParser(finalReadings);
//        new TotalTimeHistogramParser(finalReadings);
        new IntervalDurationParser(finalReadings);


    }


    /**
     * Reads the property file.
     */
    private void readProperties() {
        final Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTY_FILE));
        } catch (IOException e) {
            LOGGER.error("No properties file found! dataCollector.properties not found!");
            return;
        }

        runtimeLogFiles = properties.getProperty("runtime.log").split(",");
        uberLogFiles = properties.getProperty("uberdust.log").split(",");
        threadLogFile = properties.getProperty("listener.log");

    }

    public static void main(String[] args) {
        new TraceApp();
    }
}
