package gabia.cronMonitoring.exception.user;

public class InputNotFoundException extends RuntimeException{

    public InputNotFoundException() {
        super();
    }

    public InputNotFoundException(String message) {
        super(message);
    }

    public InputNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputNotFoundException(Throwable cause) {
        super(cause);
    }

    protected InputNotFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
