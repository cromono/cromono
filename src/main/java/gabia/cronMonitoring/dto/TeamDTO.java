package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class TeamDTO {

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        String teamAccount;
        String name;
        String userAccount;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        String teamAccount;
        String name;

        public static TeamDTO.Response from(Team team) {
            return new TeamDTO.Response(team.getAccount(), team.getName());
        }
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorResponse {

        String errorMsg;
        String errorCode;

        public ErrorResponse(String errorMsg) {
            this.errorMsg = errorMsg;
            this.errorCode = "404";
        }
    }
}
