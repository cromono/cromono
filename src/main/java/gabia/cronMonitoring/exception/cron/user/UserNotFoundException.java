package gabia.cronMonitoring.exception.cron.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotFoundException extends RuntimeException{

    private String message;

    public UserNotFoundException() {
        this.message = "Do not find User";
    }

    public UserNotFoundException(String message) {
        this.message = message;
    }

}
