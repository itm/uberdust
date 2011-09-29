package uberdust.commands;

public class NodeCapabilityCommand extends TestbedCommand{

    /**
     * Node ID.
     */
    private String nodeId;

    /**
     * Capability ID
     */
    private String capabilityId;

    /**
     * Default constructor.
     */
    public NodeCapabilityCommand() {
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
