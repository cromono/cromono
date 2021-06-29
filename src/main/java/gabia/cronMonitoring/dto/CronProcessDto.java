package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.CronProcess;
import java.sql.Timestamp;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CronProcessDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @NotEmpty @NotBlank
        String pid;
        Timestamp startTime;
        Timestamp endTime;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        UUID cronJobId;
        String pid;
        Timestamp startTime;
        Timestamp endTime;

        public static Response from(CronProcess cronProcess) {
            return new Response(cronProcess.getCronJob().getId(), cronProcess.getPid(),
                cronProcess.getStartTime(), cronProcess.getEndTime());
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
