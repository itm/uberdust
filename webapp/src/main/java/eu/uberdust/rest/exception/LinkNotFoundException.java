package eu.uberdust.rest.exception;

/**
 * Link not found exception class.
 */
public final class LinkNotFoundException extends Exception {
    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = 2540735156333123359L;

    /**
     * Constructor.
     */
    public LinkNotFoundException() {
        //empty constructor
    }

    /**
     * Constructor.
     * @param throwable throwable.
     */
    public LinkNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     * @param message message.
     */
    public LinkNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message message.
     * @param throwable throwable.
     */
    public LinkNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
