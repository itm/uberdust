package eu.uberdust.command;

public class NodeCapabilityInsertReadingCommand extends NodeCapabilityCommand {

    private String reading;

    public String getReading() {
        return reading;
    }

    public void setReading(final String reading) {
        this.reading = reading;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;
}
