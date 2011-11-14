package eu.uberdust.rest.exception;


public class InvalidTestbedIdException extends Exception {

    private static final long serialVersionUID = -6323874001967897515L;

    public InvalidTestbedIdException(){
        // empty constructor
    }

    public InvalidTestbedIdException(final Throwable throwable) {
        super(throwable);
    }

    public InvalidTestbedIdException(final String message){
        super(message);
    }
}
