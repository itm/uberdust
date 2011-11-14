package eu.uberdust.rest.exception;

public class NodeNotFoundException extends Exception {
    private static final long serialVersionUID = 7251288525325718362L;

    public NodeNotFoundException(){
        //empty constructor
    }

    public NodeNotFoundException(final Throwable throwable) {
        super(throwable);
    }
}
