package gabia.cronMonitoring.exception.cron.job;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CronJobExistedException extends RuntimeException {

    public  CronJobExistedException(String message){
        super(message);
    }
}
