package eu.uberdust.traceparser.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 12/5/11
 * Time: 7:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrNodeReading {

    /**
     * Node ID.
     */
    private final transient long thisId;

    public final static String START_TEXT = "Start";
    public final static String END_TEXT = "End";

    private final static String[] KEYS = {START_TEXT, END_TEXT, "Τ21", "Τ22", "T23", "T24", "T25", "T3", "T4", "T41", "T51", "T52", "T6",
            "T7a", "T7b", "T81", "T82", "T83", "T84", "T9", "T91", "T10", "T101"};


    private final transient Map<String, Long> timestamps = new HashMap<String, Long>();


    /**
     * Constructor.
     *
     * @param thatId the node id.
     */
    public TrNodeReading(final long thatId) {
        this.thisId = thatId;
    }

    public void addTimestamp(final String newKey, final Long newValue) {
        if (timestamps.containsKey(newKey) && timestamps.get(newKey) < newValue) {
            return;
        }
        timestamps.put(newKey, newValue);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final TrNodeReading that = (TrNodeReading) object;

        return thisId == that.thisId;
    }

    @Override
    public int hashCode() {
        int result = (int) (thisId ^ (thisId >>> 32));
        result = 31 * result + timestamps.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("TrNodeReading");
        builder.append("{ID= ").append(thisId);
        builder.append(", timestamps=[ ");
        if (isComplete()) {
            for (String key : KEYS) {
                builder.append(key).append(": ");
                builder.append(timestamps.get(key)).append(", ");
            }
        } else {
            for (String s : timestamps.keySet()) {
                builder.append(s).append(": ");
                builder.append(timestamps.get(s)).append(", ");
            }
        }
        builder.append("]}");
        builder.append("\t");
        if (timestamps.containsKey(END_TEXT) && timestamps.containsKey(START_TEXT)) {
            builder.append(timestamps.get(END_TEXT) - timestamps.get(START_TEXT));
        }
        return builder.toString();
    }

    public boolean isComplete() {
        for (String key : KEYS) {
            if (!timestamps.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public long totalDuration() {
        if (isComplete()) {
            return timestamps.get(END_TEXT) - timestamps.get(START_TEXT);
        } else {
            return 0;
        }
    }

    public long getStart() {
        return timestamps.get(START_TEXT);
    }

    public long getTime(final String interval) {
//        System.out.println(this);
//        System.out.println(timestamps.keySet());
        return timestamps.get(interval);
    }
}

