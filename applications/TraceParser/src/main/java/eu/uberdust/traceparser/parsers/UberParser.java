package eu.uberdust.traceparser.parsers;

import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 12/5/11
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class UberParser {
    /**
     * Static Logger,
     */
    private static final Logger LOGGER = Logger.getLogger(TRParser.class);
    /**
     * Text to split ids.
     */
    private static final String ID_TEXT = "ID: ";
    /**
     * The files to parse.
     */
    private final transient String[] filenames;
    /**
     * Path to files.
     */
    private final transient String path;
    /**
     * List of all node readings.
     */
    private final transient List<TrNodeReading> nodeReadings;

    /**
     * Default Constructor.
     *
     * @param path     path to files
     * @param file     the file
     * @param readings the readings
     */
    public UberParser(final String path, final String[] file, final List<TrNodeReading> readings) {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));
        LOGGER.info("UberParser initialized");
        filenames = file.clone();
        this.path = path;
        nodeReadings = readings;
        extractData();
    }

    /**
     * Read and parse data from files.
     */
    private void extractData() {
        try {
            for (String filename : filenames) {
                LOGGER.info("Parsing file: " + path + filename);
                FileInputStream fileInputStream;
                try {
                    fileInputStream = new FileInputStream(path + filename);
                } catch (Exception e) {
                    continue;
                }
                // Get the object of DataInputStream
                final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
                String strLine;
                String data;
                String timestampID;
                String[] reading;
                while ((strLine = reader.readLine()) != null) {
                    data = strLine.split(" - ")[1];
                    timestampID = data.split(" -- ")[0];
                    reading = extractReading(data.split(" -- ")[1]);
                    try {
                        final TrNodeReading trNodeReading = new TrNodeReading(Long.valueOf(reading[0]));
                        if (nodeReadings.contains(trNodeReading)) {
                            nodeReadings.get(nodeReadings.indexOf(trNodeReading)).addTimestamp(timestampID, Long.valueOf(reading[1]));
                        }
                    } catch (final NumberFormatException e) {
                        LOGGER.debug(e);
                    }
                }
                reader.close();
                dataInputStream.close();
                fileInputStream.close();
            }
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Cleanup Reading Text from string.
     * @param line the line to cleanup.
     * @return the parsed line
     */
    private String[] extractReading(final String line) {
        return line.substring(line.indexOf(ID_TEXT) + ID_TEXT.length()).split(" , ");
    }

    /**
     * get a list of all node readings.
     * @return the readings
     */
    public final List<TrNodeReading> returnReadings() {
        return nodeReadings;
    }

}

