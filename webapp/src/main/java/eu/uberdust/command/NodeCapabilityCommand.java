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
     * Capability ID
     */
    private String capabilityId = null;

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
     * @param capabilityId
     */
    public void setCapabilityId(final String capabilityId) {
        this.capabilityId = capabilityId;
    }
}
