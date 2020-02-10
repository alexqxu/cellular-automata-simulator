package cellsociety.exceptions;

public class XMLWriteException extends RuntimeException {
    public XMLWriteException(Throwable cause){super(cause);}
    public XMLWriteException(Throwable cause, String message) {
        super(message, cause);
    }
    public XMLWriteException(String message, Object... values) {
        super(String.format(message, values));
    }
}
