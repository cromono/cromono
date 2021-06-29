package gabia.cronMonitoring.exception.teamcronjob;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlreadyExistTeamCronJobException extends RuntimeException {

    private String message;

    public AlreadyExistTeamCronJobException() {
        this.message = "Already exist team cron job ";
    }

    public AlreadyExistTeamCronJobException(String message) {
        this.message = message;
    }

}
