package eu.uberdust.rest.exception;

public class TestbedNotFoundException extends Exception {

    private static final long serialVersionUID = 6579875530260581480L;

    public TestbedNotFoundException() {
        // empty constructor
    }

    public TestbedNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    public TestbedNotFoundException(final String message) {
        super(message);
    }

    public TestbedNotFoundException(final String message, final Throwable throwable) {
        super(message,throwable);
    }
}
