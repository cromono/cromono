package gabia.cronMonitoring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CronServer {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "server_ip", unique = true)
    private String ip;
}
