package gabia.cronMonitoring.dto.response;

import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInfoDTO {

    String account;
    String name;
    String email;
    UserRole role;

    public static UserInfoDTO from(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setAccount(user.getAccount());
        userInfoDTO.setName(user.getName());
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setRole(user.getRole());
        return userInfoDTO;
    }
}
