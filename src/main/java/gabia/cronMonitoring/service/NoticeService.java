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

    /**
     * 해당 유저의 모든 알림 구독 리스트 조회
     *
     * @param account 유저의 account
     * @return List<NoticeSubscriptionDTO.Response></NoticeSubscriptionDTO.Response>
     */
    public List<NoticeSubscriptionDTO.Response> findAllNoticeSubscription(String account) {

        // 모든 알림 구독 리스트 조회
        List<NoticeSubscriptionDTO.Response> responses = noticeSubscriptionRepository
            .findByRcvUserAccount(account)
            .stream()
            .map(dto -> NoticeSubscriptionDTO.Response.from(dto))
            .collect(Collectors.toList());

        return responses;
    }

    /**
     * 알림 구독을 추가합니다.
     *
     * @param account 유저의 account
     * @param request NoticeSubscriptionDTO.Request
     * @return NoticeSubscriptionDTO.Response
     * @throws CronJobNotFoundException
     * @throws UserNotFoundException
     * @throws AlreadyExistNoticeSubscriptionException
     */
    @Transactional
    public NoticeSubscriptionDTO.Response addNoticeSubscription(String account,
        NoticeSubscriptionDTO.Request request) {

        // request 에 해당하는 Cron Job, User를 존재 유무 확인
        CronJob cronJob = cronJobRepository.findById(request.getCronJobId())
            .orElseThrow(() -> new CronJobNotFoundException());

        User rcvUser = userRepository.findByAccount(request.getRcvUserId())
            .orElseThrow(() -> new UserNotFoundException("Receive User Not Found"));

        User createUser = userRepository.findByAccount(request.getCreateUserId())
            .orElseThrow(() -> new UserNotFoundException("Create User Not Found"));

        // 사용자가 이미 같은 알림이 구독되어 있을 경우 Exception 발생
        noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(account, request.getCronJobId())
            .ifPresent(present -> {
                throw new AlreadyExistNoticeSubscriptionException();
            });

        // 알림 구독 생성
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

    /**
     * 알림 구독을 삭제합니다.
     *
     * @param account   유저의 account
     * @param cronJobId Cron Job의 Id
     * @throws CronJobNotFoundException
     * @throws UserNotFoundException
     */
    @Transactional
    public void removeNoticeSubscription(String account, UUID cronJobId) {

        // 해당하는 Cron Job, User의 존재 유무 확인
        cronJobRepository.findById(cronJobId).orElseThrow(() -> new CronJobNotFoundException());
        userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException());

        // 알림 구독 삭제
        noticeSubscriptionRepository.deleteByRcvUserAccountAndCronJobId(account, cronJobId);
    }

    /**
     * 해당하는 유저의 모든 알림 리스트를 조회합니다.
     *
     * @param account 유저의 account
     * @return List<NoticeDTO.Response>
     * @throws UserNotFoundException
     */
    public List<NoticeDTO.Response> findAllNotice(String account) {

        // User 존재 유무 확인
        userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException());

        // User 가 등록한 알림의 Cron Job 리스트 추출
        List<UUID> cronJobIdList = noticeSubscriptionRepository
            .findCronJobIdByRcvUserAccount(account);

        // Notice Status를 Map으로 생성
        Map<String, NoticeStatus> stringNoticeStatusMap = noticeStatusRepository.findAll()
            .stream()
            .collect(Collectors.toMap(NoticeStatus::getRcvUserId, Function.identity()));

        // Notice Status와 Notice를 DTO로 Mapping 해 Response 생성
        List<NoticeDTO.Response> responses = noticeRepository.findByCronJobIdIn(cronJobIdList)
            .stream()
            .map(dto -> NoticeDTO.Response.from(dto, stringNoticeStatusMap.containsKey(account)))
            .collect(Collectors.toList());

        return responses;
    }

    /**
     * 알림을 선택합니다.
     *
     * @param account 유저의 account
     * @param notId   notice id
     * @return NoticeDTO.Response
     * @throws UserNotFoundException
     * @throws NoticeNotFoundException
     * @throws AlreadyExistNoticeStatusException
     */
    @Transactional
    public NoticeDTO.Response selectNotice(String account, Long notId) {

        // 유저 존재 유무 확인
        userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException());

        // Notice 조회, 해당 Notice 가 없는 경우 Exception 발생
        Notice notice = noticeRepository.findById(notId)
            .orElseThrow(() -> new NoticeNotFoundException());

        // 이미 읽은 Notice 에 대해 Exception 발생
        // NoticeController Exception Handler 에서 Http Status 를 200으로 return 하도록 처리
        noticeStatusRepository.findByRcvUserIdAndNoticeId(account, notId).ifPresent(present -> {
            throw new AlreadyExistNoticeStatusException(present);
        });

        // Notice Status를 추가, 알림을 읽은 경우
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        NoticeStatus noticeStatus = NoticeStatus.builder()
            .notice(notice)
            .rcvUserId(account)
            .readDate(timestamp)
            .build();
        noticeStatusRepository.save(noticeStatus);

        return NoticeDTO.Response.from(notice, true);
    }

    /**
     * 알림을 생성합니다.
     *
     * @param request NoticeDTO.Request
     * @return NoticeDTO.Response
     * @throws CronJobNotFoundException
     */
    @Transactional
    public NoticeDTO.Response createNotice(NoticeDTO.Request request) {

        // Cron Job 존재 유무 확인
        CronJob cronJob = cronJobRepository.findById(request.getCronJobId())
            .orElseThrow(() -> new CronJobNotFoundException());

        // Notice 생성
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
