package cellsociety.exceptions;

public class InvalidXMLStructureException extends RuntimeException{
    public InvalidXMLStructureException(Throwable cause){super(cause);}
    public InvalidXMLStructureException(Throwable cause, String message) {
        super(message, cause);
    }
}
