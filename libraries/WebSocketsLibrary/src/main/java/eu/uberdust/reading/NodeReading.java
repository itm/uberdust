package eu.uberdust.reading;

import java.io.Serializable;

/**
 * NodeReading model class.
 */
public final class NodeReading implements Serializable {

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = 6616919745525778185L;

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
     * Node reference.
     */
    private String nodeId;

    /**
     * Capability reference.
     */
    private String capabilityName;

    /**
     * String Timestamp.
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
    public NodeReading() {
        // empty constructor
    }

    /**
     * Returns testbed id.
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
     * Returns node id.
     *
     * @return node id.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets node id.
     *
     * @param nodeId node id.
     */
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
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
     * Returns the timestamp that this reading occured.
     *
     * @return timestamp of the reading.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp that this reading occured.
     *
     * @param timestamp , timestamp of the reading.
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
        restString.append("/testbed/").append(testbedId).append("/node/").append(nodeId).append("/capability/")
                .append(capabilityName).append("/insert/timestamp/").append(timestamp);

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
        delimitedString.append(NodeReading.class.getName()).append(DELIMITER).append(testbedId).append(DELIMITER)
                .append(nodeId).append(DELIMITER).append(capabilityName)
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
        return NodeReading.class.getName() + "{"
                + testbedId + ", "
                + nodeId + ", "
                + capabilityName + ", "
                + timestamp + ", "
                + ((reading != null) ? reading : "null") + ", "
                + ((stringReading != null) ? stringReading : "null") + "}";
    }
}


