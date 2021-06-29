package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.UserCronJob;
import gabia.cronMonitoring.util.ValidUUID;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserCronJobDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @ValidUUID
        UUID cronJobId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        UUID cronJobId;
        String userAccount;

        public static UserCronJobDTO.Response from(UserCronJob userCronJob) {
            return new UserCronJobDTO.Response(userCronJob.getCronJob().getId(),
                userCronJob.getUser().getAccount());
        }
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {

        String errorMsg;
        String errorCode;

        public ErrorResponse(String errorMsg) {
            this.errorMsg = errorMsg;
            this.errorCode = "409";
        }
    }


}
