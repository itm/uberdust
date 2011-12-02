package eu.uberdust.reading;

import java.io.Serializable;

/**
 * LinkReading model class.
 */
public final class LinkReading implements Serializable {


    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = -4818293656594892843L;

    /**
     * Delimiter.
     */
    private static final String DELIMITER = "@";

    /**
     * Testbed ID.
     */
    private String testbedId;

    /**
     * Link Source.
     */
    private String linkSource;

    /**
     * Link Target.
     */
    private String linkTarget;

    /**
     * Capability Name.
     */
    private String capabilityName;

    /**
     * Timestamp of the reading.
     */
    private String timestamp;

    /**
     * Numeric value of the reading.
     */
    private String reading;

    /**
     * Constructor.
     */
    public LinkReading() {
        // empty constructor
    }

    /**
     * Returns Testbed Id.
     * @return testbed id.
     */
    public String getTestbedId() {
        return testbedId;
    }

    /**
     * Sets testbed id.
     * @param testbedId testbed id.
     */
    public void setTestbedId(final String testbedId) {
        this.testbedId = testbedId;
    }

    /**
     * Returns Link Source.
     * @return link's source.
     */
    public String getLinkSource() {
        return linkSource;
    }

    /**
     * Sets link source.
     * @param linkSource link source.
     */
    public void setLinkSource(final String linkSource) {
        this.linkSource = linkSource;
    }

    /**
     * Returns link target.
     * @return link target.
     */
    public String getLinkTarget() {
        return linkTarget;
    }

    /**
     * Sets link target.
     * @param linkTarget link target.
     */
    public void setLinkTarget(final String linkTarget) {
        this.linkTarget = linkTarget;
    }

    /**
     * Returns capability name.
     * @return capability name.
     */
    public String getCapabilityName() {
        return capabilityName;
    }

    /**
     * Sets capability name.
     * @param capabilityName capability name.
     */
    public void setCapabilityName(final String capabilityName) {
        this.capabilityName = capabilityName;
    }

    /**
     * Returns capability reading.
     *
     * @return Link's capability reading
     */
    public String getReading() {
        return reading;
    }

    /**
     * Set capability reading.
     *
     * @param reading , reading value.
     */
    public void setReading(final String reading) {
        this.reading = reading;
    }

    /**
     * Returns timestamp value.
     *
     * @return Link's reading timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the reading timestamp.
     *
     * @param timestamp , a Date instance.
     */
    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns a string of the appropriate REST URL in order to insert this link reading.
     * @return a string of the appropriate REST URL in order to insert this link reading.
     */
    public String toRestString(){
        return "/testbed/" + testbedId
                + "/link/" + linkSource + "/" + linkTarget
                + "/capability/" + capabilityName
                + "/insert/timestamp/" + timestamp
                + "/reading/" + reading;
    }

    @Override
    public String toString() {
        return LinkReading.class.getName()
                + DELIMITER + testbedId
                + DELIMITER + linkSource
                + DELIMITER + linkTarget
                + DELIMITER + capabilityName
                + DELIMITER + timestamp
                + DELIMITER + reading;
    }
}
