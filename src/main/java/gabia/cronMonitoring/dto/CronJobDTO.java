package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.CronServer;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter @Setter
public class CronJobDTO {

    private UUID id;
    private String cronName;
    private String cronExpr;
    private Date minStartTime;
    private Date maxEndTime;
    private String serverIp;



}

