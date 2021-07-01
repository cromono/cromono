package gabia.cronMonitoring.dto;

import gabia.cronMonitoring.entity.NoticeSubscription;
import gabia.cronMonitoring.util.ValidUUID;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NoticeSubscriptionDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @NotEmpty @NotBlank
        String rcvUserId;
        @NotEmpty @NotBlank
        String createUserId;
        @ValidUUID
        UUID cronJobId;
        //Todo: Not_type 추가 예정

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        UUID cronJobId;
        //Todo: Not_type 추가 예정

        public static NoticeSubscriptionDTO.Response from(NoticeSubscription noticeSubscription) {
            return new NoticeSubscriptionDTO.Response(noticeSubscription.getCronJob().getId());
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
