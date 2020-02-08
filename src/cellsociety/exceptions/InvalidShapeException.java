package cellsociety.exceptions;

public class InvalidShapeException extends RuntimeException {

    public InvalidShapeException(Throwable cause) {
        super(cause);
    }

    public InvalidShapeException(Throwable cause, String message) {
        super(message, cause);
    }
}
