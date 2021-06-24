package gabia.cronMonitoring.exception.user;

public class ExistingInputException extends RuntimeException{

    public ExistingInputException() {
        super();
    }

    public ExistingInputException(String message) {
        super(message);
    }

    public ExistingInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistingInputException(Throwable cause) {
        super(cause);
    }

    protected ExistingInputException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
