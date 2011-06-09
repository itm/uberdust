package uberdust.commands;

/**
 * POJO object for holding parameters for game related commands.
 */
public class NodeCommand {

    /**
     * The Node ID.
     */
    private String nodeId;

    /**
     * Default constructor.
     */
    public NodeCommand() {
        this.nodeId = null;
    }

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