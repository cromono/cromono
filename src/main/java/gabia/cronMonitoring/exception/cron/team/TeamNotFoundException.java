package gabia.cronMonitoring.exception.cron.team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamNotFoundException extends RuntimeException {

    private String message;

    public TeamNotFoundException() {
        this.message = "Do not find Team";
    }

    public TeamNotFoundException(String message) {
        this.message = message;
    }
}
