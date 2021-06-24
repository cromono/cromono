package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TeamDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        String account;
        String name;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        String account;
        String name;
        public static TeamDTO.Response from(Team team) {
            return new TeamDTO.Response(team.getAccount(), team.getName());
        }
    }
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
