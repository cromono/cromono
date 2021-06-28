package gabia.cronMonitoring.exception.user;

public class NotValidEmailException extends RuntimeException{

    public NotValidEmailException() {
        super();
    }

    public NotValidEmailException(String message) {
        super(message);
    }

    public NotValidEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidEmailException(Throwable cause) {
        super(cause);
    }

    protected NotValidEmailException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
