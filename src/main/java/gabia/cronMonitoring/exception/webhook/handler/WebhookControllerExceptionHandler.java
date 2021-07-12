package gabia.cronMonitoring.exception.webhook.handler;

import gabia.cronMonitoring.exception.webhook.ExistingWebhookException;
import gabia.cronMonitoring.exception.webhook.NoticeSubscriptionNotFoundException;
import gabia.cronMonitoring.exception.webhook.WebhookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebhookControllerExceptionHandler {

    @ExceptionHandler(ExistingWebhookException.class)
    public ResponseEntity handle(ExistingWebhookException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoticeSubscriptionNotFoundException.class)
    public ResponseEntity handle(NoticeSubscriptionNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WebhookNotFoundException.class)
    public ResponseEntity handle(WebhookNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
