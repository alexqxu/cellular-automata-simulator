package cellsociety.exceptions;

public class InvalidGridException extends RuntimeException {
  public InvalidGridException(Throwable cause){
    super(cause);
  }
  public InvalidGridException(Throwable cause, String message){
    super(message, cause);
  }
}
