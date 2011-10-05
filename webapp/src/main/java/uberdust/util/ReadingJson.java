package uberdust.util;

public class ReadingJson {

    private double reading;

    private long timestamp;


    public ReadingJson() {

    }

    public ReadingJson(final long timestamp, final double reading) {
        this.timestamp = timestamp;
        this.reading = reading;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getReading() {
        return reading;
    }

    public void setReading(double reading) {
        this.reading = reading;
    }
}
