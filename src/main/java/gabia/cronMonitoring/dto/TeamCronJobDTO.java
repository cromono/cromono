package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.TeamCronJob;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TeamCronJobDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        UUID cronJobId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        UUID cronJobId;
        String teamAccount;

        public static TeamCronJobDTO.Response from(TeamCronJob teamCronJob) {
            return new TeamCronJobDTO.Response(teamCronJob.getCronJob().getId(),
                teamCronJob.getTeam().getAccount());
        }
    }

    @Data
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
