package cellsociety.exceptions;

/**
 * Exception thrown when the structure of the XMl is incorrect (elements are incorrect, or the contents of the elements
 * are not of the datatypes that are valid)
 * @author Alex Xu
 */
public class InvalidXMLStructureException extends RuntimeException{
    public InvalidXMLStructureException(Throwable cause){super(cause);}
    public InvalidXMLStructureException(Throwable cause, String message) {
        super(message, cause);
    }
    public InvalidXMLStructureException(String message, Object... values) {
        super(String.format(message, values));
    }
}
