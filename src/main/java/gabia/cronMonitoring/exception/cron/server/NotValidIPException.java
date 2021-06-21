package gabia.cronMonitoring.exception.cron.server;

public class NotValidIPException extends RuntimeException{

    public NotValidIPException() {
        super();
    }

    public NotValidIPException(String message) {
        super(message);
    }

    public NotValidIPException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidIPException(Throwable cause) {
        super(cause);
    }
}
