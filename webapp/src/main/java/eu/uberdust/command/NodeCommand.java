package eu.uberdust.command;

/**
 * POJO class for holding parameters for node related command.
 */
public final class NodeCommand extends TestbedCommand {

    /**
     * The Node ID.
     */
    private String nodeId = null;

    /**
     * Node's description.
     */
    private String description = null;

    /**
     * Get the ID of the node.
     *
     * @return the Node ID.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Set the ID of the node.
     *
     * @param thisId the ID of the node.
     */
    public void setNodeId(final String thisId) {
        nodeId = thisId;
    }

    /**
     * Returns the description of the node.
     *
     * @return the description of the node.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the node.
     *
     * @param description node's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
