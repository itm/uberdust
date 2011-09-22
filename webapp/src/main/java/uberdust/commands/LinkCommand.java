package uberdust.commands;

/**
 * POJO object for holding parameters for link related commands.
 */
public class LinkCommand {

    /**
     * The Source Node ID.
     */
    private String sourceId;

    /**
     * The Target Node ID.
     */
    private String targetId;

    /**
     * Default constructor.
     */
    public LinkCommand() {
        this.sourceId = null;
        this.targetId = null;
    }

    /**
     * Get the ID of the source node.
     *
     * @return the Source Node ID.
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Get the ID of the source node.
     *
     * @return the Source Node ID.
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * Set the source ID of the node
     *
     * @param thisId the ID of the source node
     */
    public void setSourceId(final String thisId) {
        sourceId = thisId;
    }

    /**
     * Set the ID of the node.
     *
     * @param thisId the ID of the target node.
     */
    public void setTargetId(final String thisId) {
        targetId = thisId;
    }
}
