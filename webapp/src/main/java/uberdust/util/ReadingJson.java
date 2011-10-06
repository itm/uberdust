package uberdust.util;

public class ReadingJson {

    private double reading;

    private long timestamp;

    public ReadingJson(final long timestamp, final double reading) {
        this.timestamp = timestamp;
        this.reading = reading;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public double getReading() {
        return reading;
    }

    public void setReading(final double reading) {
        this.reading = reading;
    }
}
