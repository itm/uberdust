package eu.uberdust.traceparser.parsers;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/5/11
 * Time: 9:43 PM
 */
public class ThreadParser {

    /**
     * Static Logger,
     */
    private static final Logger LOGGER = Logger.getLogger(ThreadParser.class);
    /**
     * Prefix that describes a thread pool statistic.
     */
    private final static String TEMPLATE_POOL = "DataCollectorChannelUpstreamHandler:85 - ";
    /**
     * Prefix that describes the message rate.
     */
    private final static String TEMPLATE_RATE = "MessageRate : ";
    /**
     * Prefix that describes the current pool size.
     */
    private final static String POOL_SIZE_PX = "PoolSize : ";
    /**
     * Prefix that describes the active threads in the pool size.
     */
    private final static String ACTIVE_SIZE_PX = "Active :";
    /**
     * date Start.
     */
    private final static String DATE = "[";
    /**
     * date End.
     */
    private final static String END = "]";

    /**
     * Default Constructor.
     *
     * @param path
     * @param threadLogFile
     */
    public ThreadParser(String path, String threadLogFile) {

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path + threadLogFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get the object of DataInputStream
        final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
        String strLine;

        final TimeSeries poolSizeSeries = new TimeSeries("Pool Size");
        final TimeSeries activeSizeSeries = new TimeSeries("Active Size");
        final TimeSeries rateSeries = new TimeSeries("Message Rate");
        try {
            while ((strLine = reader.readLine()) != null) {
                LOGGER.debug(strLine);

                if (strLine.contains(TEMPLATE_POOL)) {
                    final int poolStart = strLine.indexOf(POOL_SIZE_PX) + POOL_SIZE_PX.length();
                    final int poolStop = strLine.indexOf(' ', poolStart);
                    final String poolSize = strLine.substring(poolStart, poolStop);

                    final int activeStart = strLine.indexOf(ACTIVE_SIZE_PX) + ACTIVE_SIZE_PX.length();
                    int activeStop = strLine.indexOf(' ', activeStart);
                    if (activeStop < 0) {
                        activeStop = strLine.length();
                    }

                    final String activeSize = strLine.substring(activeStart, activeStop);
                    //System.out.println(strLine);
                    final long date = extractDate(strLine);
//                    System.out.println("date: " + date);
//                    System.out.println("Size: " + poolSize);
                    poolSizeSeries.addOrUpdate(new Millisecond(new Date(date)), Double.parseDouble(poolSize));
//                    System.out.println("Active: " + activeSize);
                    activeSizeSeries.addOrUpdate(new Millisecond(new Date(date)), Double.parseDouble(activeSize));

                } else if (strLine.contains(TEMPLATE_RATE)) {
                    final int rateStart = strLine.indexOf(TEMPLATE_RATE) + TEMPLATE_RATE.length();
                    final int rateStop = strLine.indexOf(' ', rateStart);

                    final String rate = strLine.substring(rateStart, rateStop);
                    final long date = extractDate(strLine);
                    rateSeries.addOrUpdate(new Millisecond(new Date(date)), Double.parseDouble(rate));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        final TimeSeriesCollection threadsCollection = new TimeSeriesCollection();
        threadsCollection.addSeries(poolSizeSeries);
        threadsCollection.addSeries(activeSizeSeries);
        final JFreeChart threadsChart = ChartFactory.createTimeSeriesChart(
                "Threads Used", "time", "# of Threads",
                threadsCollection, true, true, false);
        threadsChart.getXYPlot().setDomainGridlinePaint(Color.black);
        threadsChart.getXYPlot().setRangeGridlinePaint(Color.black);
        threadsChart.getPlot().setBackgroundPaint(Color.white);

        final JFrame threadsFrame = new JFrame();
        threadsFrame.add(new ChartPanel(threadsChart));
        threadsFrame.pack();
        threadsFrame.setVisible(true);


        final TimeSeriesCollection rateCollection = new TimeSeriesCollection();
        rateCollection.addSeries(rateSeries);
        final JFreeChart messagesChart = ChartFactory.createTimeSeriesChart(
                "Message Rate",
                "time",
                "# of Messages",
                rateCollection, true, true, false);
        messagesChart.getPlot().setBackgroundPaint(Color.white);
        messagesChart.getXYPlot().setDomainGridlinePaint(Color.black);
        messagesChart.getXYPlot().setRangeGridlinePaint(Color.black);
        final JFrame messagesFrame = new JFrame();
        messagesFrame.add(new ChartPanel(messagesChart));
        messagesFrame.pack();
        messagesFrame.setVisible(true);

    }

    /**
     * extracts the date from the event log.
     *
     * @param line a line from the log
     * @return date as long
     */
    private long extractDate(final String line) {
        final int dateStart = line.indexOf(DATE) + DATE.length();
        final int dateStop = line.indexOf(END, dateStart);
        final String date = line.substring(dateStart, dateStop);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss','S", Locale.US);
        try {
            final Date parseDate = dateFormat.parse(date);
            return parseDate.getTime();
        } catch (Exception e) {

        }
        return -1;
    }

//    /**
//     * starts the application.
//     *
//     * @param args user arguments
//     */
//    public static void main(String[] args) {
//
//    }
}
