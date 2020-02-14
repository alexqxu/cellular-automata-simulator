package cellsociety.exceptions;

/**
 * Exception thrown when a user requests an invalid cell
 * @author Alex Xu
 */
public class InvalidCellException extends RuntimeException {

  public InvalidCellException(Throwable cause) {
    super(cause);
  }

  public InvalidCellException(Throwable cause, String message) {
    super(message, cause);
  }
}
