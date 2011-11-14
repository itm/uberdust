package eu.uberdust.rest.exception;

public class InvalidCapabilityNameException extends Exception{

    private static final long serialVersionUID = 1550075632126163944L;

    public InvalidCapabilityNameException(){
        //empty constructor
    }

    public InvalidCapabilityNameException(final Throwable throwable){
        super(throwable);
    }
}
