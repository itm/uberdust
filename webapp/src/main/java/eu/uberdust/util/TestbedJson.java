package eu.uberdust.util;

/**
 * POJO class for holding parameters for JSON representation of a testbed.
 */
public final class TestbedJson {

    /**
     * Testbed id in persistence.
     */
    private int testbedId;

    /**
     * Testbed Name.
     */
    private String testbedName;

    /**
     * Constructor.
     */
    public TestbedJson() {
        // empty constructor
    }

    /**
     * Constructor.
     * @param testbedId testbed id.
     * @param testbedName testbed name.
     */
    public TestbedJson(final int testbedId,final String testbedName) {
        this.testbedId = testbedId;
        this.testbedName = testbedName;
    }

    /**
     * Returns testbed id.
     * @return testbed id.
     */
    public int getTestbedId() {
        return testbedId;
    }

    /**
     * Sets testbed id
     * @param testbedId testbed id.
     */
    public void setTestbedId(final int testbedId) {
        this.testbedId = testbedId;
    }

    /**
     * Returns testbed name.
     * @return testbed name.
     */
    public String getTestbedName() {
        return testbedName;
    }

    /**
     * Sets testbed name.
     * @param testbedName testbed name.
     */
    public void setTestbedName(final String testbedName) {
        this.testbedName = testbedName;
    }

}
