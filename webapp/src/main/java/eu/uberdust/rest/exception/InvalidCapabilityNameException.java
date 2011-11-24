package eu.uberdust.rest.exception;

/**
 * Invalid capability name exception.
 */
public final class InvalidCapabilityNameException extends Exception {

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = 1550075632126163944L;

    /**
     * Constructor.
     */
    public InvalidCapabilityNameException() {
        //empty constructor
    }

    /**
     * Constructor.
     * @param throwable throwable instance.
     */
    public InvalidCapabilityNameException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     * @param message message.
     */
    public InvalidCapabilityNameException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message message.
     * @param throwable throwable.
     */
    public InvalidCapabilityNameException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
