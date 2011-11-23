package eu.uberdust.rest.exception;

/**
 * Invalid Testbed Id exception class.
 */
public final class InvalidTestbedIdException extends Exception {

    /**
     * Serial Unique Version ID.
     */
    private static final long serialVersionUID = -6323874001967897515L;

    /**
     * Constructor.
     */
    public InvalidTestbedIdException() {
        // empty constructor
    }

    /**
     * Constructor.
     * @param throwable throwable.
     */
    public InvalidTestbedIdException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     * @param message message.
     */
    public InvalidTestbedIdException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message message.
     * @param throwable thrwoable.
     */
    public InvalidTestbedIdException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
