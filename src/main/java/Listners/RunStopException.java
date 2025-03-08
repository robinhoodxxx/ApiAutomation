package Listners;

public class RunStopException extends RuntimeException {


    // Default constructor
    public RunStopException() {
        super();
    }

    // Constructor with a custom message
    public RunStopException(String message) {
        super(message);
    }

    // Constructor with a custom message and a cause
    public RunStopException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with a cause
    public RunStopException(Throwable cause) {
        super(cause);
    }
}
