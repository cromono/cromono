package gabia.cronMonitoring.exception.notice.noticestatus;

import gabia.cronMonitoring.entity.Notice;
import gabia.cronMonitoring.entity.NoticeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlreadyExistNoticeStatusException extends RuntimeException {

    private String message;
    private Notice notice;

    public AlreadyExistNoticeStatusException() {
        this.message = "Already exist notice status";
    }

    public AlreadyExistNoticeStatusException(NoticeStatus noticeStatus) {
        this.message = "Already exist notice status";
        this.notice = noticeStatus.getNotice();
    }
}
