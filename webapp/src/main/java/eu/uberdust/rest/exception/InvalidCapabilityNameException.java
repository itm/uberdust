package eu.uberdust.rest.exception;

public class InvalidCapabilityNameException extends Exception {

    private static final long serialVersionUID = 1550075632126163944L;

    public InvalidCapabilityNameException() {
        //empty constructor
    }

    public InvalidCapabilityNameException(final Throwable throwable) {
        super(throwable);
    }

    public InvalidCapabilityNameException(final String message) {
        super(message);
    }

    public InvalidCapabilityNameException(final String message, final Throwable throwable) {
        super(message,throwable);
    }
}
