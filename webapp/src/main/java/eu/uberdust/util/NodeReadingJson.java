package eu.uberdust.util;

import java.util.List;

/**
 * POJO class for holding parameters for JSON representation of a Node Reading.
 */
public final class NodeReadingJson {

    /**
     * Node ID.
     */
    private String nodeId;

    /**
     * Capability ID/Name.
     */
    private String capabilityId;

    /**
     * List of Readings.
     */
    private List<ReadingJson> readings;

    /**
     * Constructor.
     */
    public NodeReadingJson() {
        //empty constructor
    }

    /**
     * Constructor.
     * @param nodeId node id.
     * @param capabilityId capability id.
     * @param readings readings list.
     */
    public NodeReadingJson(final String nodeId, final String capabilityId, final List<ReadingJson> readings) {
        this.nodeId = nodeId;
        this.capabilityId = capabilityId;
        this.readings = readings;
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
     * Returns capability id.
     * @return capability id.
     */
    public String getCapabilityId() {
        return capabilityId;
    }

    /**
     * Sets capability id.
     * @param capabilityId capability id.
     */
    public void setCapabilityId(final String capabilityId) {
        this.capabilityId = capabilityId;
    }

    /**
     * Returns readings list.
     * @return readings list.
     */
    public List<ReadingJson> getReadings() {
        return readings;
    }

    /**
     * Sets readings list.
     * @param readings readings list.
     */
    public void setReadings(final List<ReadingJson> readings) {
        this.readings = readings;
    }
}
