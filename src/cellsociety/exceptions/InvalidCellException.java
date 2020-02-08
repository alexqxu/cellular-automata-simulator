package cellsociety.exceptions;

public class InvalidCellException extends RuntimeException {

  public InvalidCellException(Throwable cause) {
    super(cause);
  }

  public InvalidCellException(Throwable cause, String message) {
    super(message, cause);
  }
}
