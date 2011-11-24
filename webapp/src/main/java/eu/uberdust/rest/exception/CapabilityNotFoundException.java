package eu.uberdust.rest.exception;

/**
 * Capability not found exception class.
 */
public final class CapabilityNotFoundException extends Exception {
    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = -7732002952370199907L;

    /**
     * Constructor.
     */
    public CapabilityNotFoundException() {
        //empty constructor
    }

    /**
     * Constructor.
     * @param throwable throwable.
     */
    public CapabilityNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     * @param message message.
     */
    public CapabilityNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message message.
     * @param throwable throwable.
     */
    public CapabilityNotFoundException(final String message, final Throwable throwable) {
        super(message,throwable);
    }
}
