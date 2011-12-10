package eu.uberdust.traceparser.parsers;

import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.JFrame;
import java.awt.Color;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/6/11
 * Time: 12:40 PM
 */
public class IntervalDurationParser {
    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(IntervalDurationParser.class);

    /**
     * Intervals to be plotted.
     */
    private final transient String[] intervals = {
            "Τ21", "Τ22", "T23", "T24", "T25", "T51", "T52",
            "T81", "T82", "T83", "T84", "T9", "T91", "T10", "T101"};
    /**
     * Threshold to exclude readings.
     */
    private static final long EXCLUDE_THRESHOLD = 100000;

    /**
     * Constructor.
     *
     * @param finalReadings the readings to use for the plot
     */
    public IntervalDurationParser(final List<TrNodeReading> finalReadings) {

        TimeSeries[] serieses = new TimeSeries[intervals.length];
        for (int i = 0; i < intervals.length; i++) {
            serieses[i] = new TimeSeries(intervals[i]);
        }
        for (final TrNodeReading finalReading : finalReadings) {
//            LOGGER.info(finalReading);
//            LOGGER.info(finalReading.getStart());
            if (finalReading.totalDuration() < EXCLUDE_THRESHOLD) {
                for (int i = 1; i < intervals.length; i++) {
                    LOGGER.debug("requesting " + intervals[i]);
                    final long duration = finalReading.getTime(intervals[i]) - finalReading.getTime(intervals[i - 1]);
                    final Millisecond eventTime = new Millisecond(new Date(finalReading.getStart()));
                    serieses[i].addOrUpdate(eventTime, duration);
                }
            }
        }

        final TimeSeriesCollection collection = new TimeSeriesCollection();
        for (int i = 1; i < intervals.length; i++) {
            collection.addSeries(serieses[i]);
        }

        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Interval Duration",
                "time",
                "Duration in Millis",
                collection, true, true, false);
        chart.getPlot().setBackgroundPaint(Color.white);
        chart.getXYPlot().setDomainGridlinePaint(Color.black);
        chart.getXYPlot().setRangeGridlinePaint(Color.black);
        final JFrame frame = new JFrame();
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);

    }
}
