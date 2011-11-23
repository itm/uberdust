package eu.uberdust.command;

/**
 * POJO class for holding parameters for testbed related command.
 */
public class TestbedCommand {

    /**
     * Identity of the network.
     */
    private String testbedId = null;

    /**
     * Returns testbed id.
     *
     * @return testbed id
     */
    public String getTestbedId() {
        return testbedId;
    }

    /**
     * Sets testbedID.
     *
     * @param testbedId a testbed id.
     */
    public void setTestbedId(final String testbedId) {
        this.testbedId = testbedId;
    }
}
