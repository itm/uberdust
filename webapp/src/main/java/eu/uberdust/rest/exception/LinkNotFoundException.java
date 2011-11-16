package eu.uberdust.rest.exception;

public class LinkNotFoundException extends Exception {

    private static final long serialVersionUID = 2540735156333123359L;

    public LinkNotFoundException() {
        //empty constructor
    }

    public LinkNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    public LinkNotFoundException(final String message) {
        super(message);
    }
}
