package gabia.cronMonitoring.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RefreshTokenDTO {

    private String refreshToken;
    private Instant expiresAt;
}
