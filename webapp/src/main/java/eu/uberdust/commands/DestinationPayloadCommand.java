package eu.uberdust.commands;

public class DestinationPayloadCommand {

    private String destination;

    private String payload;

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(final String payload) {
        this.payload = payload;
    }
}
