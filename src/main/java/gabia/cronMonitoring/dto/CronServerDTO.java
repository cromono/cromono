package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.CronServer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CronServerDTO {
    private String ip;

    public static CronServerDTO from(CronServer cronServer) {
        return new CronServerDTO(cronServer.getIp());
    }
}
