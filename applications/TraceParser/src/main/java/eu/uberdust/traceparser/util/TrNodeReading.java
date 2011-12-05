package eu.uberdust.traceparser.util;

import java.util.HashMap;

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
    private final long thisId;

    private final HashMap<String, Long> timestamps = new HashMap<String, Long>();


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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TrNodeReading that = (TrNodeReading) o;

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
        final StringBuilder sb = new StringBuilder();
        sb.append("TrNodeReading");
        sb.append("{thisId=").append(thisId);
        sb.append(", timestamps=[ ");
        for (String s : timestamps.keySet()) {
            sb.append(s).append(":");
            sb.append(timestamps.get(s)).append(", ");
        }
        sb.append("]}");
        sb.append("\t");
        if (timestamps.containsKey("End") && timestamps.containsKey("Start")) {
            sb.append(timestamps.get("End") - timestamps.get("Start"));
        }
        return sb.toString();
    }
}
