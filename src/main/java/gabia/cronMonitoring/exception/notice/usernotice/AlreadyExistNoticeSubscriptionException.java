package gabia.cronMonitoring.exception.notice.usernotice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlreadyExistNoticeSubscriptionException extends RuntimeException {

    private String message;

    public AlreadyExistNoticeSubscriptionException() {
        this.message = "Already exist notice subscription";
    }
}
