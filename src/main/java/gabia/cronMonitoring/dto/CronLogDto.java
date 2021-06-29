package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.CronLog;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CronLogDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        String cronProcess;
        Instant start;
        Instant stop;
        String value;

        public static CronLogDto.Response from(CronLog cronLog) {
            return new CronLogDto.Response(cronLog.getCronProcess(), cronLog.getStart(),
                cronLog.getStop(), cronLog.getValue());
        }
    }

}
