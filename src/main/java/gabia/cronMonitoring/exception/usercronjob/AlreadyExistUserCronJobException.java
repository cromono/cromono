package gabia.cronMonitoring.exception.usercronjob;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlreadyExistUserCronJobException extends RuntimeException {

    private String message;

    public AlreadyExistUserCronJobException() {
        this.message = "Already exist user cron job ";
    }

    public AlreadyExistUserCronJobException(String message) {
        this.message = message;
    }
}
