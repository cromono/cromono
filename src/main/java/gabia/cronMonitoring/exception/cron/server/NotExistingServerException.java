package gabia.cronMonitoring.exception.cron.server;

public class NotExistingServerException extends IllegalStateException {

    public NotExistingServerException() {
    }

    public NotExistingServerException(String s) {
        super(s);
    }

    public NotExistingServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistingServerException(Throwable cause) {
        super(cause);
    }
}
