package gabia.cronMonitoring.dto.request;

import gabia.cronMonitoring.entity.Enum.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccessDTO {

    String account;
    String name;
    String email;
    String password;
    UserRole role;
}
