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
     * String Capability reading value for this node.
     */
    private String  reading;

    /**
     * Constructor.
     */
    public NodeReading() {
        // empty constructor
    }

    /**
     * Returns testbed id.
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
     * Returns node id.
     * @return node id.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets node id.
     * @param nodeId node id.
     */
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
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
     * Returns the timestamp that this reading occured.
     *
     * @return timestamp of the reading.
     */
    public String  getTimestamp() {
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
     * Returns this reading value.
     *
     * @return this reading value.
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
     * Returns a string of the appropriate REST URL in order to insert this link reading.
     * @return a string of the appropriate REST URL in order to insert this link reading.
     */
    public String toRestString(){
        return "/testbed/" + testbedId
                + "/node/" + nodeId
                + "/capability/" + capabilityName
                + "/insert/timestamp/" + timestamp
                + "/reading/" + reading;
    }

    @Override
    public String toString() {
        return  NodeReading.class.getName()
                + DELIMITER + testbedId
                + DELIMITER + nodeId
                + DELIMITER + capabilityName
                + DELIMITER + timestamp
                + DELIMITER + reading;
    }
}

