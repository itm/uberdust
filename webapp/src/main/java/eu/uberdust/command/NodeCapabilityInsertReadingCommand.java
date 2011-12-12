package eu.uberdust.command;

/**
 * POJO class object for holding parameters for a node-capability related command.
 */
public final class NodeCapabilityInsertReadingCommand extends NodeCapabilityCommand {

    /**
     *Reading.
     */
    private String reading;

    /**
     * Timestamp.
     */
    private String timestamp;

    /**
     * String Reading.
     */
    private String stringReading;

    /**
     * Returns reading.
     * @return reading.
     */
    public String getReading() {
        return reading;
    }

    /**
     * Sets reading.
     * @param reading reading.
     */
    public void setReading(final String reading) {
        this.reading = reading;
    }

    /**
     * Returns timestamp.
     * @return timestamp.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     * @param timestamp timestamp.
     */
    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns string reading.
     * @return string reading.
     */
    public String getStringReading() {
        return stringReading;
    }

    /**
     * Sets string reading
     * @param stringReading string reading.
     */
    public void setStringReading(final String stringReading) {
        this.stringReading = stringReading;
    }
}
