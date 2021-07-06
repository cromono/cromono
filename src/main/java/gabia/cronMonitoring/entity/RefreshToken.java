package gabia.cronMonitoring.entity;

import java.io.Serializable;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

//유효기간 2주
@RedisHash(timeToLive = 1209600)
@Builder
@Data
public class RefreshToken implements Serializable {

    @Id
    private String id; //사용자

    @NotNull
    private String token;
}
