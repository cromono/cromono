package gabia.cronMonitoring.exception.webhook;

public class NoticeSubscriptionNotFoundException extends RuntimeException{

    public NoticeSubscriptionNotFoundException(String message) {
        super(message);
    }
}
