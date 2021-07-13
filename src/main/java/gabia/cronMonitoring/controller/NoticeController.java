package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.NoticeDTO;
import gabia.cronMonitoring.dto.NoticeDTO.Response;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO;
import gabia.cronMonitoring.service.NoticeService;
import gabia.cronMonitoring.util.ValidUUID;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping(path = "/notifications/users/{userId}")
    public ResponseEntity<List<NoticeSubscriptionDTO.Response>> getNoticeSubscription(
        @NotEmpty @PathVariable(value = "userId") String userId) {

        List<NoticeSubscriptionDTO.Response> allTeamCronJob = noticeService
            .findAllNoticeSubscription(userId);
        return new ResponseEntity<>(allTeamCronJob, HttpStatus.OK);
    }

    @PostMapping(path = "/notifications/users/{userId}")
    public ResponseEntity<NoticeSubscriptionDTO.Response> postNoticeSubscription(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @RequestBody @Valid NoticeSubscriptionDTO.Request request) {

        NoticeSubscriptionDTO.Response response = noticeService
            .addNoticeSubscription(userId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/notifications/users/{userId}/crons/{cronJobId}")
    public ResponseEntity<HttpStatus> deleteNoticeSubscription(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId) {

        noticeService.removeNoticeSubscription(userId, cronJobId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path = "/notifications/users/{userId}/notice")
    public ResponseEntity<List<NoticeDTO.Response>> getNoticeList(
        @NotEmpty @PathVariable(value = "userId") String userId) {

        List<Response> allNotice = noticeService.findAllNotice(userId);

        return new ResponseEntity<>(allNotice, HttpStatus.OK);
    }

    @GetMapping(path = "/notifications/users/{userId}/notice/{notId}")
    public ResponseEntity<NoticeDTO.Response> getNotice(
        @NotEmpty @PathVariable(value = "userId") String userId,
        @PathVariable(value = "notId") Long notId) {

        Response response = noticeService.selectNotice(userId, notId);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(path = "/notifications/notice")
    public ResponseEntity<NoticeDTO.Response> postNotice(
        @RequestBody @Valid NoticeDTO.Request request) {

        Response notice = noticeService.createNotice(request);

        return new ResponseEntity<>(notice, HttpStatus.OK);

    }
}
