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
     * Double Reading type.
     */
    private static final String DOUBLE_READING = "D";

    /**
     * String Reading type.
     */
    private static final String STRING_READING = "S";

    /**
     * Both reading type.
     */
    private static final String BOTH_READING = "B";

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
    private String reading = null;

    /**
     * String value for this node.
     */
    private String stringReading = null;


    /**
     * Constructor.
     */
    public LinkReading() {
        // empty constructor
    }

    /**
     * Returns Testbed Id.
     *
     * @return testbed id.
     */
    public String getTestbedId() {
        return testbedId;
    }

    /**
     * Sets testbed id.
     *
     * @param testbedId testbed id.
     */
    public void setTestbedId(final String testbedId) {
        this.testbedId = testbedId;
    }

    /**
     * Returns Link Source.
     *
     * @return link's source.
     */
    public String getLinkSource() {
        return linkSource;
    }

    /**
     * Sets link source.
     *
     * @param linkSource link source.
     */
    public void setLinkSource(final String linkSource) {
        this.linkSource = linkSource;
    }

    /**
     * Returns link target.
     *
     * @return link target.
     */
    public String getLinkTarget() {
        return linkTarget;
    }

    /**
     * Sets link target.
     *
     * @param linkTarget link target.
     */
    public void setLinkTarget(final String linkTarget) {
        this.linkTarget = linkTarget;
    }

    /**
     * Returns capability name.
     *
     * @return capability name.
     */
    public String getCapabilityName() {
        return capabilityName;
    }

    /**
     * Sets capability name.
     *
     * @param capabilityName capability name.
     */
    public void setCapabilityName(final String capabilityName) {
        this.capabilityName = capabilityName;
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
     * Returns this (double) reading value.
     *
     * @return this (double) reading value.
     */
    public String getReading() {
        return reading;
    }

    /**
     * Sets this reading value.
     *
     * @param reading , this reading value.
     */
    public void setReading(final String reading) {
        this.reading = reading;
    }

    /**
     * Returns this (string) reading value.
     *
     * @return this (string) reading value.
     */
    public String getStringReading() {
        return stringReading;
    }

    /**
     * Sets this (string) reading value.
     *
     * @param stringReading (string) reading value.
     */
    public void setStringReading(final String stringReading) {
        this.stringReading = stringReading;
    }

    /**
     * Returns a string of the appropriate REST URL in order to insert this link reading.
     *
     * @return a string of the appropriate REST URL in order to insert this link reading.
     */
    public String toRestString() {

        StringBuilder restString = new StringBuilder();

        restString.append("/testbed/").append(testbedId).append("/link/").append(linkSource).append("/")
                .append(linkTarget).append("/capability/").append(capabilityName).append("/insert/timestamp/")
                .append(timestamp);

        // if it has reading value
        if (reading != null) {
            restString.append("/reading/").append(reading);
        }

        // if  it has stringReading value
        if (stringReading != null) {
            restString.append("/stringreading/").append(stringReading);
        }

        // finish restString
        restString.append("/");
        return restString.toString();
    }

    /**
     * Returns a delimited string representation of the link reading.
     *
     * @return delimited string.
     */
    public String toDelimitedString() {

        // build string to be sent
        final StringBuilder delimitedString = new StringBuilder();
        delimitedString.append(LinkReading.class.getName()).append(DELIMITER).append(testbedId).append(DELIMITER)
                .append(linkSource).append(DELIMITER).append(linkTarget).append(DELIMITER).append(capabilityName)
                .append(DELIMITER).append(timestamp);

        // complete the string according to the type of reading . Double/String/Both.
        if (reading != null && stringReading == null) {
            delimitedString.append(DELIMITER).append(DOUBLE_READING).append(DELIMITER).append(reading);
        } else if (reading == null && stringReading != null) {
            delimitedString.append(DELIMITER).append(STRING_READING).append(DELIMITER).append(stringReading);
        } else {
             delimitedString.append(DELIMITER).append(BOTH_READING).append(DELIMITER).append(reading)
                     .append(DELIMITER).append(stringReading);
        }
        return delimitedString.toString();
    }

    /**
     * Override implementation of Object.toString().
     *
     * @return toString() for this reading.
     */
    @Override
    public String toString() {
        return LinkReading.class.getName() + "{"
                + testbedId + ", "
                + linkSource + ", "
                + linkTarget + ", "
                + capabilityName + ", "
                + timestamp + ", "
                + ((reading != null) ? reading : "null") +  ", "
                + ((stringReading != null) ? stringReading : "null") + "}";
    }
}
