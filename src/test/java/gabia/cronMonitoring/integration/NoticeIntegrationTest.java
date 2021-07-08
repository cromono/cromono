package gabia.cronMonitoring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.controller.NoticeController;
import gabia.cronMonitoring.dto.NoticeDTO;
import gabia.cronMonitoring.dto.NoticeDTO.Request;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.Enum.NoticeType;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.Notice;
import gabia.cronMonitoring.entity.NoticeSubscription;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.repository.NoticeRepository;
import gabia.cronMonitoring.repository.NoticeStatusRepository;
import gabia.cronMonitoring.repository.NoticeSubscriptionRepository;
import gabia.cronMonitoring.repository.UserRepository;
import gabia.cronMonitoring.service.NoticeService;
import java.sql.Timestamp;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active:dev")
@Transactional
public class NoticeIntegrationTest {

    private MockMvc mvc;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    NoticeSubscriptionRepository noticeSubscriptionRepository;

    @Autowired
    NoticeStatusRepository noticeStatusRepository;

    @Autowired
    NoticeService noticeService;

    @Autowired
    NoticeController noticeController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CronJobRepositoryDataJpa cronJobRepository;

    @Autowired
    CronServerRepository cronServerRepository;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUpMockMvc() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void Notice_Subscription_리스트_조회() throws Exception {
        //given
        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();
        userRepository.save(user);

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        CronJob cronJob2 = new CronJob();

        cronJob2.setCronExpr("test1");
        cronJob2.setCronName("test1");
        cronJob2.setServer(cronServer);

        CronJob savedCronJob1 = cronJobRepository.save(cronJob);
        CronJob savedCronJob2 = cronJobRepository.save(cronJob2);

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .createUser(user)
            .rcvUser(user)
            .cronJob(savedCronJob1)
            .build();

        NoticeSubscription noticeSubscription2 = NoticeSubscription.builder()
            .createUser(user)
            .rcvUser(user)
            .cronJob(savedCronJob2)
            .build();

        noticeSubscriptionRepository.save(noticeSubscription);
        noticeSubscriptionRepository.save(noticeSubscription2);

        //when

        //then
        mvc.perform(
            get("/notifications/users/{userId}", user.getAccount()))
            .andDo(print())
            .andExpect(jsonPath("$[0].cronJobId").value(savedCronJob1.getId().toString()))
            .andExpect(jsonPath("$[1].cronJobId").value(savedCronJob2.getId().toString()))
            .andExpect(status().isOk());

    }

    @Test
    public void Notice_Subscription_생성() throws Exception {
        //given
        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();
        userRepository.save(user);

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);
        CronJob savedCronJob = cronJobRepository.save(cronJob);

        //when
        NoticeSubscriptionDTO.Request request = new NoticeSubscriptionDTO.Request();
        request.setCreateUserId(user.getAccount());
        request.setRcvUserId(user.getAccount());
        request.setCronJobId(cronJob.getId());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(
            post("/notifications/users/{userId}", user.getAccount())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andDo(print())
            .andExpect(jsonPath("$.cronJobId").value(savedCronJob.getId().toString()))
            .andExpect(status().isOk());

    }

    @Test
    public void Notice_Subscription_삭제() throws Exception {
        //given
        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();
        userRepository.save(user);

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);
        cronJobRepository.save(cronJob);

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .cronJob(cronJob)
            .rcvUser(user)
            .createUser(user)
            .build();

        noticeSubscriptionRepository.save(noticeSubscription);
        //when

        //then
        mvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}", user.getAccount(),
                cronJob.getId()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void Notice_List_조회() throws Exception {

        //given
        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();
        userRepository.save(user);

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        CronJob cronJob2 = new CronJob();

        cronJob2.setCronExpr("test1");
        cronJob2.setCronName("test1");
        cronJob2.setServer(cronServer);

        CronJob savedCronJob1 = cronJobRepository.save(cronJob);
        CronJob savedCronJob2 = cronJobRepository.save(cronJob2);

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .createUser(user)
            .rcvUser(user)
            .cronJob(savedCronJob1)
            .build();

        NoticeSubscription noticeSubscription2 = NoticeSubscription.builder()
            .createUser(user)
            .rcvUser(user)
            .cronJob(savedCronJob2)
            .build();

        noticeSubscriptionRepository.save(noticeSubscription);
        noticeSubscriptionRepository.save(noticeSubscription2);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Notice notice = Notice.builder()
            .noticeCreateDateTime(timestamp)
            .noticeType(NoticeType.End)
            .noticeMessage("test Message")
            .cronJob(cronJob)
            .build();

        Notice notice2 = Notice.builder()
            .noticeCreateDateTime(timestamp)
            .noticeType(NoticeType.End)
            .noticeMessage("test Message2")
            .cronJob(cronJob)
            .build();

        Notice savedNotice = noticeRepository.save(notice);
        Notice savedNotice2 = noticeRepository.save(notice2);

        //when

        //then
        mvc.perform(
            get("/notifications/users/{userId}/notice/", user.getAccount()))
            .andDo(print())
            .andExpect(jsonPath("$[0].noticeId").value(savedNotice.getId().toString()))
            .andExpect(jsonPath("$[1].noticeId").value(savedNotice2.getId().toString()))
            .andExpect(jsonPath("$[0].cronJobId").value(cronJob.getId().toString()))
            .andExpect(jsonPath("$[1].cronJobId").value(cronJob.getId().toString()))
            .andExpect(status().isOk());
    }

    @Test
    public void Notice_select_하는_경우() throws Exception {

        //given
        User user = User.builder()
            .account("test")
            .email("test@gmail.com")
            .name("test")
            .password("test")
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();
        userRepository.save(user);

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        CronJob savedCronJob1 = cronJobRepository.save(cronJob);

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .createUser(user)
            .rcvUser(user)
            .cronJob(savedCronJob1)
            .build();

        noticeSubscriptionRepository.save(noticeSubscription);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Notice notice = Notice.builder()
            .noticeCreateDateTime(timestamp)
            .noticeType(NoticeType.End)
            .noticeMessage("test Message")
            .cronJob(cronJob)
            .build();

        Notice savedNotice = noticeRepository.save(notice);

        //when

        //then
        mvc.perform(
            get("/notifications/users/{userId}/notice/{notId}", user.getAccount(),
                savedNotice.getId()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void Notice_생성_하는_경우() throws Exception {

        //given
        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();

        cronJob.setCronExpr("test1");
        cronJob.setCronName("test1");
        cronJob.setServer(cronServer);

        cronJobRepository.save(cronJob);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //when
        NoticeDTO.Request request = new Request();
        request.setNoticeMessage("test");
        request.setNoticeType(NoticeType.NoStart);
        request.setCronJobId(cronJob.getId());
        request.setNoticeCreateDateTime(timestamp);

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(
            post("/notifications/notice").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andDo(print())
            .andExpect(jsonPath("$.cronJobId").value(cronJob.getId().toString()))
            .andExpect(jsonPath("$.noticeType").value(NoticeType.NoStart.toString()))
            .andExpect(jsonPath("$.noticeMessage").value("test"))
            .andExpect(status().isOk());
    }
}
