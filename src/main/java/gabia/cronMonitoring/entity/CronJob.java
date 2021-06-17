package gabia.cronMonitoring.entity;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "cron_job_id", columnDefinition = "BINARY(16)")
    private UUID id;


    @Column(name = "cron_name")
    @NotNull
    private String cronName;

    @Column(name = "cron_expr")
    @NotNull
    private String cronExpr;

    @Temporal(TemporalType.TIME)
    private Date minStartTime;
    @Temporal(TemporalType.TIME)
    private Date maxEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_ip")
    private CronServer server;
}
