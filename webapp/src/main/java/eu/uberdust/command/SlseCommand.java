package eu.uberdust.command;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 1/20/12
 * Time: 12:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class SlseCommand {

    /**
     * The Slse ID.
     */
    private String slseId = null;

    /**
     * Get the ID of the node.
     *
     * @return the Node ID.
     */
    public String getSlseId() {
        return slseId;
    }

    /**
     * Set the ID of the node.
     *
     * @param thisId the ID of the node.
     */
    public void setSlseId(final String thisId) {
        slseId = thisId;
    }

    private String testbedId;

    public String getTestbedId() {
        return testbedId;
    }

    public void setTestbedId(String testbedId) {
        this.testbedId = testbedId;
    }
}
