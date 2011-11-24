package eu.uberdust.rest.exception;

/**
 * Invalid limit class exception.
 */
public final class InvalidLimitException extends Exception {

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = -8281065522969254473L;

    /**
     * Constructor.
     * @param message message.
     * @param throwable throwable.
     */
    public InvalidLimitException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
