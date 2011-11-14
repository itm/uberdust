package eu.uberdust.rest.exception;

public class InvalidNodeIdException extends Exception{

    private static final long serialVersionUID = 1550075632126163944L;

    public InvalidNodeIdException(){
        //empty constructor
    }

    public InvalidNodeIdException(final Throwable throwable){
        super(throwable);
    }
}
