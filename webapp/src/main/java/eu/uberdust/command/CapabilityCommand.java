package eu.uberdust.command;

/**
 * POJO class for holding parameters for capability related command.
 */
public final class CapabilityCommand extends TestbedCommand {

    /**
     * Capability Name.
     */
    private String capabilityName;

    /**
     * Capability description.
     */
    private String description;

    /**
     * Returns capability name.
     * @return capability name.
     */
    public String getCapabilityName() {
        return capabilityName;
    }

    /**
     * Sets capability name.
     * @param capabilityName capability name.
     */
    public void setCapabilityName(final String capabilityName) {
        this.capabilityName = capabilityName;
    }

    /**
     * Returns description.
     *
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description description.
     */
    public void setDescription(final String description) {
        this.description = description;
    }
}
