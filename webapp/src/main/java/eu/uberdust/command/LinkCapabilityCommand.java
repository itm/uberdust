package eu.uberdust.command;

/**
 * POJO class for holding parameters for a link-capability related command.
 */
public class LinkCapabilityCommand extends TestbedCommand {

    /**
     * Source ID.
     */
    private String sourceId = null;

    /**
     * Target ID.
     */
    private String targetId = null;

    /**
     * Capability ID.
     */
    private String capabilityId = null;

    /**
     * returned Reading's limit for this node/capability.
     */
    private String readingsLimit = null;

    /**
     * Returns the target id of the link.
     *
     * @return the target id of the link.
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * Sets the target id of the link.
     *
     * @param targetId target id of the link.
     */
    public void setTargetId(final String targetId) {
        this.targetId = targetId;
    }

    /**
     * Returns the source id of the link.
     *
     * @return the source id of the link.
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * Sets the source id of the link.
     *
     * @param sourceId source id of the link.
     */
    public void setSourceId(final String sourceId) {
        this.sourceId = sourceId;
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
