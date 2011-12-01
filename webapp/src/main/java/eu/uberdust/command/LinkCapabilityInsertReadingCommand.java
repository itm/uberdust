package eu.uberdust.command;

/**
 * POJO class object for holding parameters for a link-capability related command.
 */
public final class LinkCapabilityInsertReadingCommand extends LinkCapabilityCommand {
    /**
     *Reading.
     */
    private String reading;

    /**
     * Timestamp.
     */
    private String timestamp;

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
}
