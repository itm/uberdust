package eu.uberdust.rest.exception;

/**
 * Node not found exception class.
 */
public final class NodeNotFoundException extends Exception {

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = 7251288525325718362L;

    /**
     * Constructor.
     */
    public NodeNotFoundException() {
        //empty constructor
    }

    /**
     * Constructor.
     * @param throwable throwable.
     */
    public NodeNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     * @param message message,
     */
    public NodeNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message message.
     * @param throwable throwable.
     */
    public NodeNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
