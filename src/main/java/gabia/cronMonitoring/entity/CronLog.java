package gabia.cronMonitoring.entity;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Measurement(name = "cron_log")
public class CronLog {

    @Column(timestamp = true)
    private Instant logTime;

    @Column(tag = true)
    private String cronProcess;

    @Column
    private Instant start;

    @Column
    private Instant stop;

    @Column
    private String value;

    @Column
    private Instant time;
}
