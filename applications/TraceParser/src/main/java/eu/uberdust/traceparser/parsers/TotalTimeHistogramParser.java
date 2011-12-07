package eu.uberdust.traceparser.parsers;

import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/6/11
 * Time: 12:35 PM
 */
public class TotalTimeHistogramParser {
    /**
     * Static Logger,
     */
    private static final Logger LOGGER = Logger.getLogger(TotalTimeHistogramParser.class);

    public TotalTimeHistogramParser(ArrayList<TrNodeReading> finalReadings) {
        long max = -1;
        for (TrNodeReading finalReading : finalReadings) {
            LOGGER.debug(finalReading);
            LOGGER.debug(finalReading.getStart());
            if (finalReading.totalDuration() < 100000) {
                if (max < finalReading.totalDuration()) {
                    max = finalReading.totalDuration();
                }
            }
        }

        max = max / 100 + 1;
        XYSeries histogram = new XYSeries("");
        double values[] = new double[(int) max];
        for (int i = 0; i < max; i++) {
            values[i]++;
        }
        int i = 0;
        for (TrNodeReading finalReading : finalReadings) {
            if (finalReading.totalDuration() < 100000) {
                values[((int) (finalReading.totalDuration() / 100))]++;
            }
        }
        for (int j = 0; j < max; j++) {
            LOGGER.debug("POS " + j + " = " + values[j]);
            histogram.addOrUpdate(j * 100, values[j]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
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
