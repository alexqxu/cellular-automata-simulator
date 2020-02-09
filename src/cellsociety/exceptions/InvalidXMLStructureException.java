package cellsociety.exceptions;

public class InvalidXMLStructureException extends RuntimeException{
    public InvalidXMLStructureException(Throwable cause){super(cause);}
    public InvalidXMLStructureException(Throwable cause, String message) {
        super(message, cause);
    }
    public InvalidXMLStructureException(String message, Object... values) {
        super(String.format(message, values));
    }
}
