package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import gabia.cronMonitoring.dto.NoticeDTO;
import gabia.cronMonitoring.dto.NoticeDTO.Response;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO.Request;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.Enum.NoticeType;
import gabia.cronMonitoring.entity.Enum.UserRole;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private NoticeSubscriptionRepository noticeSubscriptionRepository;

    @Mock
    private NoticeStatusRepository noticeStatusRepository;

    @Mock
    private CronJobRepositoryDataJpa cronJobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    void findAllNoticeSubscription() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        CronJob cronJob2 = new CronJob();

        cronJob2.setId(UUID.randomUUID());
        cronJob2.setCronExpr("test1");
        cronJob2.setCronName("test1");
        cronJob2.setServer(cronServer);

        List<NoticeSubscription> responses = new LinkedList<>();

        NoticeSubscription noticeSubscription1 = NoticeSubscription.builder()
            .id(1L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();

        NoticeSubscription noticeSubscription2 = NoticeSubscription.builder()
            .id(2L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob2)
            .build();

        responses.add(noticeSubscription1);
        responses.add(noticeSubscription2);

        given(noticeSubscriptionRepository.findByRcvUserAccount("test"))
            .willReturn(responses);

        //when
        List<NoticeSubscriptionDTO.Response> allNoticeSubList = noticeService
            .findAllNoticeSubscription("test");

        //then
        Assertions.assertThat(allNoticeSubList.get(0).getCronJobId()).isEqualTo(cronJob.getId());
        Assertions.assertThat(allNoticeSubList.get(1).getCronJobId()).isEqualTo(cronJob2.getId());
    }

    @Test
    void addNoticeSubscription_성공() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));

        given(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId())).willReturn(
            Optional.empty());

        given(noticeSubscriptionRepository.save(any(NoticeSubscription.class))).willAnswer(
            AdditionalAnswers.returnsFirstArg());

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());
        request.setRcvUserId(user.getAccount());
        request.setCreateUserId(user.getAccount());

        NoticeSubscriptionDTO.Response response = noticeService
            .addNoticeSubscription("test", request);

        //then
        Assertions.assertThat(response.getCronJobId()).isEqualTo(cronJob.getId());

    }

    @Test
    void addNoticeSubscription_크론잡이_없는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));

        given(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId())).willReturn(
            Optional.empty());

        given(noticeSubscriptionRepository.save(any(NoticeSubscription.class))).willAnswer(
            AdditionalAnswers.returnsFirstArg());

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());
        request.setRcvUserId(user.getAccount());
        request.setCreateUserId(user.getAccount());

        //then
        assertThrows(CronJobNotFoundException.class, () -> {
            NoticeSubscriptionDTO.Response response = noticeService
                .addNoticeSubscription("test", request);
        });

    }

    @Test
    void addNoticeSubscription_유저가_없는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.empty());
        given(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId())).willReturn(
            Optional.empty());
        given(noticeSubscriptionRepository.save(any(NoticeSubscription.class))).willAnswer(
            AdditionalAnswers.returnsFirstArg());

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());
        request.setRcvUserId(user.getAccount());
        request.setCreateUserId(user.getAccount());

        //then
        assertThrows(UserNotFoundException.class, () -> {
            NoticeSubscriptionDTO.Response response = noticeService
                .addNoticeSubscription("test", request);
        });

    }

    @Test
    void addNoticeSubscription_Subscription이_이미_있는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        NoticeSubscription noticeSubscription1 = NoticeSubscription.builder()
            .id(1L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));
        given(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId())).willReturn(
            Optional.of(noticeSubscription1));
        given(noticeSubscriptionRepository.save(any(NoticeSubscription.class))).willAnswer(
            AdditionalAnswers.returnsFirstArg());

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());
        request.setRcvUserId(user.getAccount());
        request.setCreateUserId(user.getAccount());

        //then
        assertThrows(AlreadyExistNoticeSubscriptionException.class, () -> {
            noticeService.addNoticeSubscription("test", request);
        });

    }


    @Test
    void removeNoticeSubscription_성공() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        //when
        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));

        noticeService.removeNoticeSubscription(user.getAccount(), cronJob.getId());

    }

    @Test
    void removeNoticeSubscription_크론잡이_없는경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        //when
        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));

        assertThrows(CronJobNotFoundException.class, () -> {
            noticeService.removeNoticeSubscription(user.getAccount(), cronJob.getId());
        });

    }

    @Test
    void removeNoticeSubscription_유저가_없는경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        //when
        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            noticeService.removeNoticeSubscription(user.getAccount(), cronJob.getId());
        });

    }

    @Test
    void findAllNotice_성공() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        CronJob cronJob2 = new CronJob();

        cronJob2.setId(UUID.randomUUID());
        cronJob2.setCronExpr("test1");
        cronJob2.setCronName("test1");
        cronJob2.setServer(cronServer);

        List<Notice> responses = new LinkedList<>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Notice notice1 = Notice.builder()
            .id(1L)
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();

        Notice notice2 = Notice.builder()
            .id(2L)
            .noticeMessage("test message2")
            .cronJob(cronJob2)
            .noticeType(NoticeType.End)
            .noticeCreateDateTime(timestamp)
            .build();

        responses.add(notice1);
        responses.add(notice2);

        List<UUID> cronJobIdList = new LinkedList<>();
        cronJobIdList.add(cronJob.getId());
        cronJobIdList.add(cronJob2.getId());

        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));
        given(noticeSubscriptionRepository.findCronJobIdByRcvUserAccount(user.getAccount()))
            .willReturn(cronJobIdList);
        given(noticeRepository.findByCronJobIdIn(cronJobIdList))
            .willReturn(responses);

        //when
        List<NoticeDTO.Response> allNotice = noticeService.findAllNotice("test");

        //then
        Assertions.assertThat(allNotice.get(0).getCronJobId()).isEqualTo(cronJob.getId());
        Assertions.assertThat(allNotice.get(1).getCronJobId()).isEqualTo(cronJob2.getId());
        Assertions.assertThat(allNotice.get(0).getNoticeType()).isEqualTo(NoticeType.Start);
        Assertions.assertThat(allNotice.get(1).getNoticeType()).isEqualTo(NoticeType.End);
        Assertions.assertThat(allNotice.get(0).getNoticeMessage())
            .isEqualTo(notice1.getNoticeMessage());
        Assertions.assertThat(allNotice.get(1).getNoticeMessage())
            .isEqualTo(notice2.getNoticeMessage());
    }

    @Test
    void findAllNotice_유저가_없는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        CronJob cronJob2 = new CronJob();

        cronJob2.setId(UUID.randomUUID());
        cronJob2.setCronExpr("test1");
        cronJob2.setCronName("test1");
        cronJob2.setServer(cronServer);

        List<Notice> responses = new LinkedList<>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Notice notice1 = Notice.builder()
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();

        Notice notice2 = Notice.builder()
            .noticeMessage("test message2")
            .cronJob(cronJob2)
            .noticeType(NoticeType.End)
            .noticeCreateDateTime(timestamp)
            .build();

        responses.add(notice1);
        responses.add(notice2);

        List<UUID> cronJobIdList = new LinkedList<>();
        cronJobIdList.add(cronJob.getId());
        cronJobIdList.add(cronJob2.getId());

        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.empty());
        given(noticeSubscriptionRepository.findCronJobIdByRcvUserAccount(user.getAccount()))
            .willReturn(cronJobIdList);
        given(noticeRepository.findByCronJobIdIn(cronJobIdList))
            .willReturn(responses);

        //when

        //then
        assertThrows(UserNotFoundException.class, () -> {
            noticeService.findAllNotice(user.getAccount());
        });


    }

    @Test
    void selectNotice_성공() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Notice notice = Notice.builder()
            .id(1L)
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));
        given(noticeRepository.findById(notice.getId())).willReturn(Optional.of(notice));
        given(noticeStatusRepository
            .findByRcvUserIdAndNoticeId(user.getAccount(), notice.getId()))
            .willReturn(Optional.empty());
        given(noticeStatusRepository.save(any(NoticeStatus.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());
        //when

        Response response = noticeService.selectNotice(user.getAccount(), notice.getId());

        //then
        Assertions.assertThat(response.getCronJobId()).isEqualTo(cronJob.getId());
        Assertions.assertThat(response.getNoticeMessage()).isEqualTo(notice.getNoticeMessage());
        Assertions.assertThat(response.getNotId()).isEqualTo(notice.getId());
        Assertions.assertThat(response.getNoticeCreateDateTime())
            .isEqualTo(notice.getNoticeCreateDateTime());
        Assertions.assertThat(response.getNoticeType()).isEqualTo(notice.getNoticeType());
    }

    @Test
    void selectNotice_유저가_없는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Notice notice = Notice.builder()
            .id(1L)
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.empty());
        given(noticeRepository.findById(notice.getId())).willReturn(Optional.of(notice));
        given(noticeStatusRepository
            .findByRcvUserIdAndNoticeId(user.getAccount(), notice.getId()))
            .willReturn(Optional.empty());
        given(noticeStatusRepository.save(any(NoticeStatus.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());
        //when

        //then

        assertThrows(UserNotFoundException.class, () -> {
            noticeService.selectNotice(user.getAccount(), notice.getId());
        });
    }

    @Test
    void selectNotice_Notice가_없는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Notice notice = Notice.builder()
            .id(1L)
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();
        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));
        given(noticeRepository.findById(notice.getId())).willReturn(Optional.empty());
        given(noticeStatusRepository
            .findByRcvUserIdAndNoticeId(user.getAccount(), notice.getId()))
            .willReturn(Optional.empty());
        given(noticeStatusRepository.save(any(NoticeStatus.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());
        //when

        //then
        assertThrows(NoticeNotFoundException.class, () -> {
            noticeService.selectNotice(user.getAccount(), notice.getId());
        });
    }

    @Test
    void selectNotice_Notice_Status가_있는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Notice notice = Notice.builder()
            .id(1L)
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();

        NoticeStatus noticeStatus = NoticeStatus.builder()
            .rcvUserId(user.getAccount())
            .notice(notice)
            .id(1L)
            .readDate(timestamp)
            .build();

        given(userRepository.findByAccount(user.getAccount())).willReturn(Optional.of(user));
        given(noticeRepository.findById(notice.getId())).willReturn(Optional.of(notice));
        given(noticeStatusRepository
            .findByRcvUserIdAndNoticeId(user.getAccount(), notice.getId()))
            .willReturn(Optional.of(noticeStatus));
        given(noticeStatusRepository.save(any(NoticeStatus.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());
        //when

        //then
        assertThrows(AlreadyExistNoticeStatusException.class, () -> {
            noticeService.selectNotice(user.getAccount(), notice.getId());
        });
    }

    @Test
    void createNotice_성공() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Notice notice = Notice.builder()
            .id(1L)
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(noticeRepository.save(any(Notice.class)))
            .willReturn(notice);

        //when
        NoticeDTO.Request request = new NoticeDTO.Request();
        request.setNoticeType(NoticeType.Start);
        request.setNoticeCreateDateTime(timestamp);
        request.setCronJobId(cronJob.getId());
        request.setNoticeMessage("test message");

        Response response = noticeService.createNotice(request);

        //then
        Assertions.assertThat(response.getCronJobId()).isEqualTo(cronJob.getId());
        Assertions.assertThat(response.getNoticeMessage()).isEqualTo(notice.getNoticeMessage());
        Assertions.assertThat(response.getNotId()).isEqualTo(notice.getId());
        Assertions.assertThat(response.getNoticeType()).isEqualTo(notice.getNoticeType());

    }

    @Test
    void createNotice_크론잡이_없는_경우() {
        //given
        openMocks(this);

        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();

        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Notice notice = Notice.builder()
            .id(1L)
            .noticeMessage("test message")
            .cronJob(cronJob)
            .noticeType(NoticeType.Start)
            .noticeCreateDateTime(timestamp)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
        given(noticeRepository.save(any(Notice.class)))
            .willReturn(notice);

        //when
        NoticeDTO.Request request = new NoticeDTO.Request();
        request.setNoticeType(NoticeType.Start);
        request.setNoticeCreateDateTime(timestamp);
        request.setCronJobId(cronJob.getId());
        request.setNoticeMessage("test message");

        //then
        assertThrows(CronJobNotFoundException.class, () -> {
            noticeService.createNotice(request);
        });

    }

}