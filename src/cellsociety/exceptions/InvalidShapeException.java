package cellsociety.exceptions;

/**
 * Exception thrown when an invalid shape is requested (e.g. Circle)
 * @author Alex Xu
 */
public class InvalidShapeException extends RuntimeException {

    public InvalidShapeException(Throwable cause) {
        super(cause);
    }

    public InvalidShapeException(Throwable cause, String message) {
        super(message, cause);
    }
}
