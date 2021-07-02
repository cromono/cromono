package gabia.cronMonitoring.entity;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

//유효기간 2주
@RedisHash(timeToLive = 1209600)
@Builder
@Getter
public class RefreshToken implements Serializable {

    @Id
    @Indexed
    private String id; //엑세스 토큰

    @NotNull
    @Indexed
    private String token;

    @NotNull
    @Indexed
    private String userAccount;

    @NotNull
    Instant createdDate;
}
