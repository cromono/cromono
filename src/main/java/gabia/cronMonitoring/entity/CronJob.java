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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@Table(name = "cron_job")
public class CronJob {

    @Id
    @Column(name = "cron_job_id")
    private UUID id;

    @Column(name = "cron_name")
    @NotNull
    private String cronName;

    @Column(name = "cron_expr")
    @NotNull
    private String cronExpr;
    private Timestamp minStartTime;
    private Timestamp maxEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_ip")
    private CronServer server;
}
