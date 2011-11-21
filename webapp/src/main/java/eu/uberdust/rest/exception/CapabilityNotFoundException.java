package eu.uberdust.rest.exception;

public class CapabilityNotFoundException extends Exception {

    private static final long serialVersionUID = -7732002952370199907L;

    public CapabilityNotFoundException() {
        //empty constructor
    }

    public CapabilityNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    public CapabilityNotFoundException(final String message) {
        super(message);
    }

    public CapabilityNotFoundException(final String message, final Throwable throwable) {
        super(message,throwable);
    }
}
