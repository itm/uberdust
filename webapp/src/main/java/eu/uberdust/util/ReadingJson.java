package eu.uberdust.util;

/**
 * POJO class for holding parameters for JSON representation of a reading.
 */
public final class ReadingJson {
    /**
     *
     */
    private double reading;
    /**
     *
     */
    private long timestamp;

    /**
     * Constructor.
     * @param timestamp timestamp.
     * @param reading reading.
     */
    public ReadingJson(final long timestamp, final double reading) {
        this.timestamp = timestamp;
        this.reading = reading;
    }

    /**
     * Return timestamp.
     * @return timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     * @param timestamp timestamp.
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns reading.
     * @return reading.
     */
    public double getReading() {
        return reading;
    }

    /**
     * Sets reading.
     * @param reading reading.
     */
    public void setReading(final double reading) {
        this.reading = reading;
    }
}
