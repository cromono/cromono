package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.Enum.NoticeType;
import gabia.cronMonitoring.entity.Notice;
import gabia.cronMonitoring.util.ValidUUID;
import java.util.Date;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NoticeDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @ValidUUID
        UUID cronJobId;

        NoticeType noticeType;

        @NotEmpty @NotBlank
        String noticeMessage;

        Date noticeCreateDateTime;

        //Todo: Not_type 추가 예정

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        Long notId;
        UUID cronJobId;
        NoticeType noticeType;
        String noticeMessage;
        Date noticeCreateDateTime;
        Boolean isRead;
        //Todo: Not_type 추가 예정

        public static NoticeDTO.Response from(Notice notice, Boolean isRead) {
            return new NoticeDTO.Response(notice.getId(), notice.getCronJob().getId(),
                notice.getNoticeType(), notice.getNoticeMessage(),
                notice.getNoticeCreateDateTime(), isRead);
        }
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {

        String errorMsg;
        String errorCode;

        public ErrorResponse(String errorMsg) {
            this.errorMsg = errorMsg;
            this.errorCode = "404";
        }
    }


}
