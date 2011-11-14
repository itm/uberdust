package eu.uberdust.util;

import java.util.List;

public class NodeReadingJson {

    private String nodeId;

    private String capabilityId;

    private List<ReadingJson> readings;

    public NodeReadingJson() {
        //empty constructor
    }

    public NodeReadingJson(final String nodeId, final String capabilityId, final List<ReadingJson> readings) {
        this.nodeId = nodeId;
        this.capabilityId = capabilityId;
        this.readings = readings;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(final String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public List<ReadingJson> getReadings() {
        return readings;
    }

    public void setReadings(final List<ReadingJson> readings) {
        this.readings = readings;
    }
}
