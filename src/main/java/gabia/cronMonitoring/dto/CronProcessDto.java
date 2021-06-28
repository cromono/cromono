package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.CronProcess;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CronProcessDto {

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        String pid;
        Timestamp startTime;
        Timestamp endTime;
    }

    @Data
    @Getter
    @Setter
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
