package gabia.cronMonitoring.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cron_server")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CronServer implements Serializable {

    @Id
    @Column(name = "cron_server_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "server_ip",unique = true)
    @NotNull
    private String ip;

    public CronServer(String ip) {
        this.ip = ip;
    }
}
