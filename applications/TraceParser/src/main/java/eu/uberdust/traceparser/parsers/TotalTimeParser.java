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
 * Time: 12:29 PM
 */
public class TotalTimeParser {
    /**
     * Static Logger,
     */
    private static final Logger LOGGER = Logger.getLogger(TotalTimeParser.class);
        /**
     * Threshold to exclude readings.
     */
    private static final long EXCLUDE_THRESHOLD = 100000;

    /**
     * Constructor.
     * @param finalReadings the readings to plot
     */
    public TotalTimeParser(final List<TrNodeReading> finalReadings) {
        final TimeSeries series = new TimeSeries("Total Duration");
        long max = -1;
        for (TrNodeReading finalReading : finalReadings) {
            LOGGER.info(finalReading);
            LOGGER.info(finalReading.getStart());
            if (finalReading.totalDuration() < EXCLUDE_THRESHOLD) {
                series.addOrUpdate(new Millisecond(new Date(finalReading.getStart())), finalReading.totalDuration());
                if (max < finalReading.totalDuration()) {
                    max = finalReading.totalDuration();
                }
            }
        }


        final TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(series);
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Total Duration",
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
