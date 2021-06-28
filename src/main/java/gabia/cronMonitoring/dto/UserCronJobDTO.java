package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.UserCronJob;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserCronJobDTO {

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        UUID cronJobId;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        UUID cronJobId;
        String UserId;

        public static UserCronJobDTO.Response from(UserCronJob userCronJob) {
            return new UserCronJobDTO.Response(userCronJob.getCronJob().getId(),
                userCronJob.getUser().getAccount());
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
