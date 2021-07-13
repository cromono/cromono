package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.request.WebhookDTO;
import gabia.cronMonitoring.dto.response.WebhookInfoDTO;
import gabia.cronMonitoring.service.WebhookSubscriptionService;
import gabia.cronMonitoring.util.ValidUUID;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebhookController {

    private final WebhookSubscriptionService webhookSubscriptionService;

    @GetMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks")
    public ResponseEntity<List<WebhookInfoDTO>> getWebhooks(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId) {
        List<WebhookInfoDTO> webhooks = webhookSubscriptionService.getWebhooks(userId, cronJobId);

        ResponseEntity responseEntity = new ResponseEntity(webhooks, HttpStatus.OK);
        return responseEntity;
    }

    @PostMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks")
    public ResponseEntity<WebhookInfoDTO> addWebhook(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId,
        @RequestBody @Valid WebhookDTO request) {
        WebhookInfoDTO webhookInfoDTO = webhookSubscriptionService.addWebhook(userId, cronJobId, request);

        ResponseEntity responseEntity = new ResponseEntity(webhookInfoDTO, HttpStatus.CREATED);
        return responseEntity;
    }

    @PatchMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}")
    public ResponseEntity<WebhookInfoDTO> updateWebhook(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId,
        @Valid @PathVariable(value = "webhookId") Long webhookId,
        @RequestBody @Valid WebhookDTO request) {
        WebhookInfoDTO webhookInfoDTO = webhookSubscriptionService
            .updateWebhook(userId, cronJobId, webhookId, request);

        ResponseEntity responseEntity = new ResponseEntity(webhookInfoDTO, HttpStatus.OK);
        return responseEntity;
    }

    @DeleteMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}")
    public ResponseEntity deleteWebhook(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId,
        @Valid @PathVariable(value = "webhookId") Long webhookId) {
        webhookSubscriptionService.deleteWebhookById(webhookId);

        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
        return responseEntity;
    }

    @DeleteMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks")
    public ResponseEntity deleteWebhooks(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId) {
        webhookSubscriptionService.deleteWebhooks(userId, cronJobId);

        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
        return responseEntity;
    }
}
