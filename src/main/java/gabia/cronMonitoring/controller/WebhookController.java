package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.request.WebhookSubscriptionDTO;
import gabia.cronMonitoring.dto.response.WebhookDTO;
import gabia.cronMonitoring.service.WebhookSubscriptionService;
import gabia.cronMonitoring.util.ValidUUID;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookSubscriptionService webhookSubscriptionService;

    @GetMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks")
    public ResponseEntity<List<WebhookDTO>> getWebhooks(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId) {
        webhookSubscriptionService.getWebhooks(userId, cronJobId);
    }

    @PostMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks")
    public ResponseEntity<WebhookDTO> addWebhook(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId,
        @RequestBody @Valid WebhookSubscriptionDTO request) {
    }

    @PatchMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks")
    public ResponseEntity<WebhookDTO> updateWebhook(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId,
        @RequestBody @Valid WebhookSubscriptionDTO request) {
    }

    @DeleteMapping(value = "/notifications/users/{userId}/crons/{cronJobId}/webhooks")
    public ResponseEntity deleteWebhook(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId,
        @RequestBody @Valid WebhookSubscriptionDTO request) {
    }
}
