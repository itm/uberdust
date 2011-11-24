package eu.uberdust.command;

/**
 * POJO class for holding parameters for a send payload-to-destination command.
 */
public final class DestinationPayloadCommand {

    /**
     * Destination.
     */
    private String destination;

    /**
     * Paylod.
     */
    private String payload;

    /**
     * Sets destination.
     * @param destination destination.
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }

    /**
     * Returns destination.
     * @return destination.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns payload.
     * @return payload.
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Sets payload.
     * @param payload payload.
     */
    public void setPayload(final String payload) {
        this.payload = payload;
    }
}
