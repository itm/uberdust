package eu.uberdust.traceparser.parsers;

import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.ArrayList;

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

    private final String[] filenames;// = "/home/akribopo/Projects/uberdust/tr.out";

    private final ArrayList<TrNodeReading> nodeReadings;

    private final static String ID = "ID: ";
    private final String path;


    /**
     * Default Constructor.
     *
     * @param path
     * @param file     the file
     * @param readings the readings
     */
    public UberParser(String path, final String[] file, final ArrayList<TrNodeReading> readings) {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("UberParser initialized");
        filenames = file;
        this.path = path;
        nodeReadings = readings;
        extractData();
    }

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
                        //LOGGER.error(e);
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

    private String[] extractReading(final String line) {
        return line.substring(line.indexOf(ID) + ID.length()).split(" , ");
    }

    public ArrayList<TrNodeReading> returnReadings() {
        return nodeReadings;
    }

}

