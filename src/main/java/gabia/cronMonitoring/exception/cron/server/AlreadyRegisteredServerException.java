package gabia.cronMonitoring.exception.cron.server;

public class AlreadyRegisteredServerException extends IllegalStateException {

    public AlreadyRegisteredServerException() {
    }

    public AlreadyRegisteredServerException(String s) {
        super(s);
    }

    public AlreadyRegisteredServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyRegisteredServerException(Throwable cause) {
        super(cause);
    }
}
