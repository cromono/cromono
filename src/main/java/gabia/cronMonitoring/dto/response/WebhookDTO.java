package gabia.cronMonitoring.dto.response;

import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import gabia.cronMonitoring.entity.WebhookSubscription;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WebhookDTO {

    @NotBlank
    WebhookEndpoint endPoint;

    @NotBlank
    String url;

    public static WebhookDTO from(WebhookSubscription webhookSubscription) {
        WebhookDTO webhookDTO = WebhookDTO.builder()
            .endPoint(webhookSubscription.getEndpoint())
            .url(webhookSubscription.getUrl())
            .build();
        return webhookDTO;
    }
}
