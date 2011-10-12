package eu.uberdust.commands;

/**
 * POJO object for holding parameters for node related commands.
 */
public class NodeCommand extends TestbedCommand {

    /**
     * The Node ID.
     */
    private String nodeId = null;

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

}