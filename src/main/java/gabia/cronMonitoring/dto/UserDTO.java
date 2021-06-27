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
        String name;
        String email;
        UserRole role;

        public static Response from(User user) {
            Response response = new Response();
            response.setAccount(user.getAccount());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            return response;
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
