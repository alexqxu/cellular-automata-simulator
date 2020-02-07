package cellsociety.exceptions;

public class InvalidFileException extends RuntimeException{

    public InvalidFileException(Throwable cause, String message, Object ... values){
        super(String.format(message,values),cause);
    }

    public InvalidFileException (String message, Object ... values) {
        super(String.format(message, values));
    }

    public InvalidFileException (Throwable cause){
        super(cause);
    }
}