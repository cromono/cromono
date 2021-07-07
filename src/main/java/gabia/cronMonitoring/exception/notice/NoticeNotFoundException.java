package gabia.cronMonitoring.exception.notice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeNotFoundException extends RuntimeException {

    private String message;

    public NoticeNotFoundException() {
        this.message = "Do not find notice";
    }
}
