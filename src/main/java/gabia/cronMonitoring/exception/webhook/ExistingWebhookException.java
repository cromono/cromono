package gabia.cronMonitoring.exception.webhook;

public class ExistingWebhookException extends RuntimeException {

    public ExistingWebhookException(String message) {
        super(message);
    }
}
