package gabia.cronMonitoring.exception.cron.process;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronJobNotFoundException extends RuntimeException {

    private String message;

    public CronJobNotFoundException() {
        this.message = "Do not find Cron Job";
    }

    public CronJobNotFoundException(String message) {
        this.message = message;
    }


}
