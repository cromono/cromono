package gabia.cronMonitoring.dto.response;

import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import gabia.cronMonitoring.entity.WebhookSubscription;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WebhookInfoDTO {

    @NotNull
    Long id;

    @NotNull
    WebhookEndpoint endPoint;

    @NotNull
    String url;

    public static WebhookInfoDTO from(WebhookSubscription webhookSubscription) {
        WebhookInfoDTO webhookInfoDTO = WebhookInfoDTO.builder()
            .id(webhookSubscription.getId())
            .endPoint(webhookSubscription.getEndpoint())
            .url(webhookSubscription.getUrl())
            .build();
        return webhookInfoDTO;
    }
}
