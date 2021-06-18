package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.CronServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CronServerDTO {
    private String serverIp;

    public static CronServerDTO from(CronServer cronServer) {
        return new CronServerDTO(cronServer.getIp());
    }
}
