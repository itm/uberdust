package eu.uberdust.rest.exception;

/**
 * Testbed not found exception class.
 */
public final class TestbedNotFoundException extends Exception {

    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = 6579875530260581480L;

    /**
     * Constructor.
     */
    public TestbedNotFoundException() {
        // empty constructor
    }

    /**
     * Constructor.
     * @param throwable throwable.
     */
    public TestbedNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Constructor.
     * @param message message.
     */
    public TestbedNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message message.
     * @param throwable throwable.
     */
    public TestbedNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
