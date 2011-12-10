package eu.uberdust.traceparser.parsers;


import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 12/5/11
 * Time: 7:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class TRParser {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TRParser.class);

    /**
     * filenames to parse.
     */
    private final transient String[] filenames;
    /**
     * path to files to parse.
     */
    private final transient String path;
    /**
     * Text part indicator.
     */
    private static final String TEXT = "Text [";
    /**
     * Date part indicator.
     */
    private static final String DATE = "Time [";
    /**
     * End part indicator.
     */
    private static final String END = "]";
    /**
     * list of all node readings.
     */
    private final transient List<TrNodeReading> nodeReadings = new ArrayList<TrNodeReading>();


    /**
     * Default Constructor.
     *
     * @param path path to the files
     * @param file the file
     */
    public TRParser(final String path, final String[] file) {
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));
        this.path = path;
        filenames = file.clone();
        LOGGER.info("TRParser initialized");
        extractData();
    }

    /**
     * Extracts readings from the files.
     */
    private void extractData() {
        try {
            for (String filename : filenames) {
                LOGGER.info("Parsing file: " + path + filename);
                final FileInputStream fileInputStream = new FileInputStream(path + filename);
                // Get the object of DataInputStream
                final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
                String strLine;

                while ((strLine = reader.readLine()) != null) {

                    final TrNodeReading thisNodeReading = new TrNodeReading(extractID(strLine));
                    if (nodeReadings.contains(thisNodeReading)) {
                        final TrNodeReading savedNodeReading = nodeReadings.get(nodeReadings.indexOf(thisNodeReading));
                        if (strLine.contains("id::0x1ccd EM_E")) {
                            savedNodeReading.addTimestamp(TrNodeReading.START_TEXT, extractDate(strLine));
                        } else if (strLine.contains("FORWARDING to 0x494")) {
                            savedNodeReading.addTimestamp(TrNodeReading.END_TEXT, extractDate(strLine));
                        }
                    } else {
                        if (strLine.contains("id::0x1ccd EM_E")) {
                            thisNodeReading.addTimestamp(TrNodeReading.START_TEXT, extractDate(strLine));
                        } else if (strLine.contains("FORWARDING to 0x494")) {
                            thisNodeReading.addTimestamp(TrNodeReading.END_TEXT, extractDate(strLine));
                        }
                        nodeReadings.add(thisNodeReading);
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
     * Extracts the reading ID from text.
     *
     * @param line the text to parse
     * @return the reading ID
     */
    private Long extractID(final String line) {
        final int textStart = line.indexOf(TEXT) + TEXT.length();
        final int textStop = line.indexOf(END, textStart);
        final String[] text = line.substring(textStart, textStop).split(" ");
        return Long.valueOf(text[text.length - 1]);
    }

    /**
     * Extracts the date of the reading from the text.
     *
     * @param line the text to parse
     * @return the date of the event as long
     */
    private long extractDate(final String line) {
        final int dateStart = line.indexOf(DATE) + DATE.length();
        final int dateStop = line.indexOf("+02:00" + END, dateStart);
        final String date = line.substring(dateStart, dateStop);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S", Locale.US);
        try {
            final Date parseDate = dateFormat.parse(date);
            return parseDate.getTime();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return -1;
    }

    /**
     * returns the readings parsed from the files.
     *
     * @return a list of all readings
     */
    public final List<TrNodeReading> returnReadings() {
        return nodeReadings;
    }

}
