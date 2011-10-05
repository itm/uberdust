package uberdust.util;

import java.util.List;

public class NodeReadingJson {

    private String nodeId;

    private String capabilityId;

    private List<ReadingJson> readings;

    public NodeReadingJson() {

    }

    public NodeReadingJson(final String nodeId, final String capabilityId, final List<ReadingJson> readings) {
        this.nodeId = nodeId;
        this.capabilityId = capabilityId;
        this.readings = readings;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public List<ReadingJson> getReadings() {
        return readings;
    }

    public void setReadings(List<ReadingJson> readings) {
        this.readings = readings;
    }
}
