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
}
