package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.NoticeDTO;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.Notice;
import gabia.cronMonitoring.entity.NoticeStatus;
import gabia.cronMonitoring.entity.NoticeSubscription;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.user.UserNotFoundException;
import gabia.cronMonitoring.exception.notice.NoticeNotFoundException;
import gabia.cronMonitoring.exception.notice.noticestatus.AlreadyExistNoticeStatusException;
import gabia.cronMonitoring.exception.notice.usernotice.AlreadyExistNoticeSubscriptionException;
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.NoticeRepository;
import gabia.cronMonitoring.repository.NoticeStatusRepository;
import gabia.cronMonitoring.repository.NoticeSubscriptionRepository;
import gabia.cronMonitoring.repository.UserRepository;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeSubscriptionRepository noticeSubscriptionRepository;
    private final UserRepository userRepository;
    private final CronJobRepositoryDataJpa cronJobRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeStatusRepository noticeStatusRepository;

    public List<NoticeSubscriptionDTO.Response> findAllNoticeSubscription(String account) {

        List<NoticeSubscriptionDTO.Response> responses = noticeSubscriptionRepository
            .findByRcvUserAccount(account)
            .stream()
            .map(dto -> NoticeSubscriptionDTO.Response.from(dto))
            .collect(Collectors.toList());

        return responses;
    }

    @Transactional
    public NoticeSubscriptionDTO.Response addNoticeSubscription(String account,
        NoticeSubscriptionDTO.Request request) {

        CronJob cronJob = cronJobRepository.findById(request.getCronJobId())
            .orElseThrow(() -> new CronJobNotFoundException());

        User rcvUser = userRepository.findByAccount(request.getRcvUserId())
            .orElseThrow(() -> new UserNotFoundException("Receive User Not Found"));

        User createUser = userRepository.findByAccount(request.getCreateUserId())
            .orElseThrow(() -> new UserNotFoundException("Create User Not Found"));

        noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(account, request.getCronJobId())
            .ifPresent(present -> {
                throw new AlreadyExistNoticeSubscriptionException();
            });

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .createUser(createUser)
            .rcvUser(rcvUser)
            .cronJob(cronJob)
            .build();

        NoticeSubscription savedNoticeSubscription = noticeSubscriptionRepository
            .save(noticeSubscription);

        Response response = Response.from(savedNoticeSubscription);

        return response;
    }

    @Transactional
    public void removeNoticeSubscription(String account, UUID cronJobId) {

        cronJobRepository.findById(cronJobId).orElseThrow(() -> new CronJobNotFoundException());
        userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException());

        noticeSubscriptionRepository.deleteByRcvUserAccountAndCronJobId(account, cronJobId);
    }

    public List<NoticeDTO.Response> findAllNotice(String account) {

        userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException());

        List<UUID> cronJobIdList = noticeSubscriptionRepository
            .findCronJobIdByRcvUserAccount(account);

        //Notice Status를 Map으로 생성
        Map<String, NoticeStatus> stringNoticeStatusMap = noticeStatusRepository.findAll()
            .stream()
            .collect(Collectors.toMap(NoticeStatus::getRcvUserId, Function.identity()));

        List<NoticeDTO.Response> responses = noticeRepository.findByCronJobIdIn(cronJobIdList)
            .stream()
            .map(dto -> NoticeDTO.Response.from(dto, stringNoticeStatusMap.containsKey(account)))
            .collect(Collectors.toList());

        return responses;
    }

    @Transactional
    public NoticeDTO.Response selectNotice(String account, Long notId) {

        userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException());

        Notice notice = noticeRepository.findById(notId)
            .orElseThrow(() -> new NoticeNotFoundException());

        noticeStatusRepository.findByRcvUserIdAndNoticeId(account, notId).ifPresent(present -> {
            throw new AlreadyExistNoticeStatusException(present);
        });

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        NoticeStatus noticeStatus = NoticeStatus.builder()
            .notice(notice)
            .rcvUserId(account)
            .readDate(timestamp)
            .build();

        noticeStatusRepository.save(noticeStatus);

        return NoticeDTO.Response.from(notice, true);
    }

    @Transactional
    public NoticeDTO.Response createNotice(NoticeDTO.Request request) {

        CronJob cronJob = cronJobRepository.findById(request.getCronJobId())
            .orElseThrow(() -> new CronJobNotFoundException());

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Notice savedNotice = Notice.builder()
            .cronJob(cronJob)
            .noticeMessage(request.getNoticeMessage())
            .noticeType(request.getNoticeType())
            .noticeCreateDateTime(timestamp)
            .build();

        Notice save = noticeRepository.save(savedNotice);

        return NoticeDTO.Response.from(save, false);

    }

}
