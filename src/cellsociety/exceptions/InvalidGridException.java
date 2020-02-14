package cellsociety.exceptions;

/**
 * Exception thrown when there is a general issue with the Grid requested.
 * @author Alex Xu
 */
public class InvalidGridException extends RuntimeException {

  public InvalidGridException(Throwable cause) {
    super(cause);
  }

  public InvalidGridException(Throwable cause, String message) {
    super(message, cause);
  }
}
