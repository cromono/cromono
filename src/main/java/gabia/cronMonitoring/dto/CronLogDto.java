package gabia.cronMonitoring.dto;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CronLogDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        String pid;
        Timestamp startTime;
        Timestamp endTime;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        UUID cronJobId;
        String pid;
        Timestamp startTime;
        Timestamp endTime;
    }

}
