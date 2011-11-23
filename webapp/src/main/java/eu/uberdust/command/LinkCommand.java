package eu.uberdust.command;

/**
 * POJO class for holding parameters for link related command.
 */
public final class LinkCommand extends TestbedCommand {

    /**
     * The Source Node ID.
     */
    private String sourceId = null;

    /**
     * The Target Node ID.
     */
    private String targetId = null;


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
     * Set the source ID of the node.
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
