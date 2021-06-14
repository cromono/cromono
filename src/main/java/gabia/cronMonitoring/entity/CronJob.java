package gabia.cronMonitoring.entity;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
public class CronJob {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "cron_job_id")
    private UUID id;
    private String cronName;
    private String cronExpr;
    private Timestamp minStartTime;
    private Timestamp maxEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_ip")
    private CronServer server;
}
