package eu.uberdust.command;

/**
 * POJO class for holding parameters for a node-capability related command.
 */
public class NodeCapabilityCommand extends TestbedCommand {

    /**
     * Node ID.
     */
    private String nodeId = null;

    /**
     * Capability ID.
     */
    private String capabilityId = null;

    /**
     * returned Reading's limit for this node/capability.
     */
    private String readingsLimit = null;


    /**
     * Get the ID of the Node or Link.
     *
     * @return the Node ID.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the ID of the Node Or Link.
     *
     * @param nodeId the ID of the node.
     */
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Returns the Id of capability.
     *
     * @return the Id of capability.
     */
    public String getCapabilityId() {
        return capabilityId;
    }

    /**
     * Set the Id of capability.
     *
     * @param capabilityId the ID of capability.
     */
    public void setCapabilityId(final String capabilityId) {
        this.capabilityId = capabilityId;
    }

    /**
     * Returns the readingsLimit of readings.
     *
     * @return the readingsLimit of readings.
     */
    public String getReadingsLimit() {
        return readingsLimit;
    }

    /**
     * Sets the readingsLimit.
     * @param readingsLimit the readingsLimit of readings
     */
    public void setReadingsLimit(final String readingsLimit) {
        this.readingsLimit = readingsLimit;
    }
}
