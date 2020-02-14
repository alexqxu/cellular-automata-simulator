package cellsociety.exceptions;

/**
 * Exception thrown when the XML writing process is interrupted or critically wrong (e.g. Out of disk space)
 * @author Alex Xu
 */
public class XMLWriteException extends RuntimeException {
    public XMLWriteException(Throwable cause){super(cause);}
    public XMLWriteException(Throwable cause, String message) {
        super(message, cause);
    }
    public XMLWriteException(String message, Object... values) {
        super(String.format(message, values));
    }
}
