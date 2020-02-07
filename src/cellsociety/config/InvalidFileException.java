package cellsociety.config;

public class InvalidFileException extends RuntimeException{

    public InvalidFileException(Throwable cause, String message, Object ... values){
        super(String.format(message,values),cause);
    }

    public InvalidFileException (String message, Object ... values) {
        super(String.format(message, values));
    }

}

