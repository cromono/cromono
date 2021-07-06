package gabia.cronMonitoring.dto.request;

import gabia.cronMonitoring.entity.Enum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserAuthDTO {

    String account;
    String name;
    String email;
    String password;
    UserRole role;
}
