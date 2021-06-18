package gabia.cronMonitoring.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronProcessNotFoundException extends RuntimeException{

    private String message;

    public CronProcessNotFoundException() {
        this.message = "Do not find Cron Process";
    }

    public CronProcessNotFoundException(String message) {
        this.message = message;
    }

}
