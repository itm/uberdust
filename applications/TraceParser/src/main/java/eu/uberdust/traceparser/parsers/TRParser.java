package eu.uberdust.traceparser.parsers;


import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
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
     * Static Logger,
     */
    private static final Logger LOGGER = Logger.getLogger(TRParser.class);

    private final String[] filenames;// = "/home/akribopo/Projects/uberdust/tr.out";

    private final static String URN = "Source [";
    private final static String TEXT = "Text [";
    private final static String DATE = "Time [";
    private final static String END = "]";

    private final ArrayList<TrNodeReading> nodeReadings = new ArrayList<TrNodeReading>();

    /**
     * Default Constructor.
     *
     * @param file the file
     */
    public TRParser(final String[] file) {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        filenames = file;
        LOGGER.info("TRParser initialized");
        extractData();
    }

    private void extractData() {
        try {
            for (String filename : filenames) {
                LOGGER.info("Parsing file: " + filename);
                final FileInputStream fileInputStream = new FileInputStream(filename);
                // Get the object of DataInputStream
                final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
                String strLine;

                while ((strLine = reader.readLine()) != null) {

                    final TrNodeReading thisNodeReading = new TrNodeReading(extractID(strLine));
                    if (nodeReadings.contains(thisNodeReading)) {
                        final TrNodeReading savedNodeReading = nodeReadings.get(nodeReadings.indexOf(thisNodeReading));
                        if (strLine.contains("id::0x1ccd EM_E")) {
                            savedNodeReading.addTimestamp(TrNodeReading.START, extractDate(strLine));
                        } else if (strLine.contains("FORWARDING to 0x494")) {
                            savedNodeReading.addTimestamp(TrNodeReading.END, extractDate(strLine));
                        }
                    } else {
                        if (strLine.contains("id::0x1ccd EM_E")) {
                            thisNodeReading.addTimestamp(TrNodeReading.START, extractDate(strLine));
                        } else if (strLine.contains("FORWARDING to 0x494")) {
                            thisNodeReading.addTimestamp(TrNodeReading.END, extractDate(strLine));
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

    private String extractNodeUrn(final String line) {
        final int nodeurnStart = line.indexOf(URN) + URN.length();
        final int nodeurnStop = line.indexOf(END, nodeurnStart);
        return line.substring(nodeurnStart, nodeurnStop);
    }

    private Long extractID(final String line) {
        final int text_start = line.indexOf(TEXT) + TEXT.length();
        final int text_stop = line.indexOf(END, text_start);
        final String[] text = line.substring(text_start, text_stop).split(" ");
        return Long.valueOf(text[text.length - 1]);
    }

    private long extractDate(final String line) {
        final int date_start = line.indexOf(DATE) + DATE.length();
        final int date_stop = line.indexOf("+02:00" + END, date_start);
        final String date = line.substring(date_start, date_stop);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'S", Locale.US);
        try {
            final Date parseDate = dateFormat.parse(date);
            return parseDate.getTime();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return -1;
    }


    public ArrayList<TrNodeReading> returnReadings() {
        return nodeReadings;
    }

}
