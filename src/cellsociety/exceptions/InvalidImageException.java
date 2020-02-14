package cellsociety.exceptions;

/**
 * Exception thrown when the Image requested is invalid (unable to be read as pixels)
 * @author Alex Xu
 */
public class InvalidImageException extends RuntimeException {
    public InvalidImageException(Throwable cause) {
        super(cause);
    }

    public InvalidImageException(Throwable cause, String message) {
        super(message, cause);
    }
}
