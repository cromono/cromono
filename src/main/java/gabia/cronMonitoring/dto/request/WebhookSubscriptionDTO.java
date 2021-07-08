package gabia.cronMonitoring.dto.request;

import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WebhookSubscriptionDTO {

    @NotBlank
    WebhookEndpoint endPoint;

    @NotBlank
    String url;
}
