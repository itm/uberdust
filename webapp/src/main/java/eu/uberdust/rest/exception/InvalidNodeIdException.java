package eu.uberdust.rest.exception;

/**
 * Invalid node id class exception.
 */
public final class InvalidNodeIdException extends Exception {

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = 1550075632126163944L;

    /**
     * Constructor.
     */
    public InvalidNodeIdException() {
        //empty constructor
    }

    /**
     * Constructor.
     * @param throwable throwable.
     */
    public InvalidNodeIdException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     * @param message message.
     */
    public InvalidNodeIdException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message message.
     * @param throwable throwable.
     */
    public InvalidNodeIdException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
