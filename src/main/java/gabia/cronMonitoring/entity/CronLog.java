package gabia.cronMonitoring.entity;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CronLog {

    @Id
    @GeneratedValue
    private Timestamp logTime;
    private String log;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pid")
    private CronProcess cronProcess;
}
