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
@Measurement(name = "cron_log")
public class CronLog {

    @Column(timestamp = true)
    private Instant logTime;

    @Column(tag = true)
    private String cronProcess;

    @Column(timestamp = true)
    private Instant start;

    @Column(timestamp = true)
    private Instant stop;

    @Column
    private String value;
}
