package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Request {

        String account;
        String name;
        String email;
        String password;
        UserRole role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Response {

        String account;

        public static Response from(User user) {
            return new Response(user.getAccount());
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ErrorResponse {
        String errorMsg;
        String errorCode;

        public ErrorResponse(String errorMsg) {
            this.errorMsg = errorMsg;
            this.errorCode = "404";
        }
    }
}
