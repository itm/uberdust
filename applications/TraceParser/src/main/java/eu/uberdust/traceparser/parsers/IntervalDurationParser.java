package eu.uberdust.traceparser.parsers;

import eu.uberdust.traceparser.util.TrNodeReading;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/6/11
 * Time: 12:40 PM
 */
public class IntervalDurationParser {
    /**
     * Static Logger,
     */
    private static final Logger LOGGER = Logger.getLogger(IntervalDurationParser.class);

    private String[] intervals = {
            "T21", "T22", "T23", "T24", "T25",
            "T3",
            "T4", "T41",
            "T51", "T52",
            "T6",
            "T7a", "T7b",
            "T81", "T82", "T83", "T84",
            "T9", "T91",
            "T10", "T101"};

    public IntervalDurationParser(ArrayList<TrNodeReading> finalReadings) {
//        final TimeSeries series[
//        intervals.length];
//        long max = -1;
//        for (TrNodeReading finalReading : finalReadings) {
//            LOGGER.info(finalReading);
//            LOGGER.info(finalReading.getStart());
//            if (finalReading.totalDuration() < 100000) {
//                series.addOrUpdate(new Millisecond(new Date(finalReading.getStart())), finalReading.totalDuration());
//                if (max < finalReading.totalDuration()) {
//                    max = finalReading.totalDuration();
//                }
//            }
//        }
//
//
//        final TimeSeriesCollection collection = new TimeSeriesCollection();
//        collection.addSeries(series);
//        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
//                "Total Duration",
//                "time",
//                "Duration in Millis",
//                collection, true, true, false);
//        chart.getPlot().setBackgroundPaint(Color.white);
//        chart.getXYPlot().setDomainGridlinePaint(Color.black);
//        chart.getXYPlot().setRangeGridlinePaint(Color.black);
//        final JFrame frame = new JFrame();
//        frame.add(new ChartPanel(chart));
//        frame.pack();
//        frame.setVisible(true);

    }
}
