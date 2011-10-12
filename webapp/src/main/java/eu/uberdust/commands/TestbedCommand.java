package eu.uberdust.commands;

/**
 * POJO object for holding parameters for testbed related commands.
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
     * Sets testbedID .
     *
     * @param testbedId , a testbed id.
     */
    public void setTestbedId(final String testbedId) {
        this.testbedId = testbedId;
    }
}
