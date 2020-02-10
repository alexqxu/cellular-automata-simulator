package cellsociety.exceptions;

public class InvalidDimensionsException extends RuntimeException{
    public InvalidDimensionsException(Throwable cause) {
        super(cause);
    }

    public InvalidDimensionsException(Throwable cause, String message) {
        super(message, cause);
    }
    public InvalidDimensionsException(String message, Object... values) {
        super(String.format(message, values));
    }
}
