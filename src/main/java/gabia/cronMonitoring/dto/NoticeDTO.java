package gabia.cronMonitoring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import gabia.cronMonitoring.entity.Enum.NoticeType;
import gabia.cronMonitoring.entity.Notice;
import gabia.cronMonitoring.util.ValidUUID;
import java.sql.Timestamp;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        Timestamp noticeCreateDateTime;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        Long noticeId;
        UUID cronJobId;
        NoticeType noticeType;
        String noticeMessage;
        Timestamp noticeCreateDateTime;
        Boolean isRead;

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
