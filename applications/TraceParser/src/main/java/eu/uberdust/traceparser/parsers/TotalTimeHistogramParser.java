package eu.uberdust.traceparser.parsers;

import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.JFrame;
import java.awt.Color;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/6/11
 * Time: 12:35 PM
 */
public class TotalTimeHistogramParser {
    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TotalTimeHistogramParser.class);
    /**
     * Threshold to exclude readings.
     */
    private static final long EXCLUDE_THRESHOLD = 100000;
    /**
     * Interval to split.
     */
    private static final int INTERVAL = 100;


    /**
     * Constructor.
     *
     * @param finalReadings the readings to represent
     */
    public TotalTimeHistogramParser(final List<TrNodeReading> finalReadings) {
        long max = -1;
        for (TrNodeReading finalReading : finalReadings) {
            LOGGER.debug(finalReading);
            LOGGER.debug(finalReading.getStart());
            if (finalReading.totalDuration() < EXCLUDE_THRESHOLD) {
                LOGGER.debug("event");
                if (max < finalReading.totalDuration()) {
                    max = finalReading.totalDuration();
                }
            }
        }

        max = max / INTERVAL + 1;
        final XYSeries histogram = new XYSeries("");
        double values[] = new double[(int) max];
        for (int i = 0; i < max; i++) {
            values[i]++;
        }
        for (TrNodeReading finalReading : finalReadings) {
            if (finalReading.totalDuration() < EXCLUDE_THRESHOLD) {
                values[((int) (finalReading.totalDuration() / INTERVAL))]++;
            }
        }
        for (int j = 0; j < max; j++) {
            LOGGER.debug("POS " + j + " = " + values[j]);
            histogram.addOrUpdate(j * INTERVAL, values[j]);
        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(histogram);
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Duration Histogram",
                "Response Time",
                "# of Events",
                dataset, PlotOrientation.VERTICAL,
                true, true, false);

        chart.getPlot().setBackgroundPaint(Color.white);
        chart.getXYPlot().setDomainGridlinePaint(Color.black);
        chart.getXYPlot().setRangeGridlinePaint(Color.black);
        final JFrame frame = new JFrame();
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
