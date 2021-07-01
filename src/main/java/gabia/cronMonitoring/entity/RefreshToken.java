package gabia.cronMonitoring.entity;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

//유효기간 2주
@RedisHash(timeToLive = 1209600)
@Builder
@Getter
public class RefreshToken implements Serializable {

    @Id
    private String id;

    @NotNull
    private String token;

    @NotNull
    Instant createdDate;
}
