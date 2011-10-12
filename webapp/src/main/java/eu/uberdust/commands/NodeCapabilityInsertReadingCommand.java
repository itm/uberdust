package eu.uberdust.commands;

public class NodeCapabilityInsertReadingCommand extends NodeCapabilityCommand {

    private String reading;

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;
}
