package uberdust.commands;

public class DestinationPayloadCommand {
    private String destination;

    private String payload;

    public DestinationPayloadCommand() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
