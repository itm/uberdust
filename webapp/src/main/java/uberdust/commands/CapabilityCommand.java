package uberdust.commands;

public class CapabilityCommand {

    /**
     * Node ID.
     */
    private String nodeId;

    /**
     * Link ID.
     */
    private String linkId;

    /**
     * Capability ID
     */
    private String capabilityId;

    /**
     * Default constructor.
     */
    public CapabilityCommand() {
        this.capabilityId = null;
        this.nodeId = null;
    }

    /**
     * Get the ID of the Node or Link.
     *
     * @return the Node ID.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Set the ID of the Node Or Link.
     *
     * @param nodeId the ID of the node.
     */
    public void setNodeId(final String nodeId) {
         this.nodeId = nodeId;
    }

    /**
     * Get the Id of link.
     * @return
     */
    public String getLinkId() {
        return linkId;
    }

    /**
     * Set the Id of link.
     * @param linkId
     */
    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    /**
     * Get the Id of capability.
     * @return
     */
    public String getCapabilityId() {
        return capabilityId;
    }

    /**
     * Set the Id of capability.
     * @param capabilityId
     */
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }
}
