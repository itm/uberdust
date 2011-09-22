package uberdust.commands;

/**
 * POJO object for holding parameters for testbed/setup related commands.
 */
public class TestbedSetupCommand {

    /**
     * Identity of the network.
     */
    private int testbedId;

    /**
     * Name of the testbed.
     */
    private String name;

    /**
     *  Identity of the setup
     */
    private int setupId;

    /**
     * Returns setup id.
     * @return  setup id
     */
    public int getSetupId() {
        return setupId;
    }

    /**
     * Sets the setup id.
     * @param setupId , a setup id.
     */
    public void setSetupId(final int setupId) {
        this.setupId = setupId;
    }

    /**
     * Returns testbed id.
     * @return testbed id
     */
    public int getTestbedId() {
        return testbedId;
    }

    /**
     * Sets testbedID .
     * @param testbedId , a testbed id.
     */
    public void setTestbedId(final int testbedId) {
        this.testbedId = testbedId;
    }

    /**
     * Returns Name of Testbed.
     * @return name of testbed
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of testbed
     * @param name , a name of testbed.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
