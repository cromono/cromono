package gabia.cronMonitoring.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.request.WebhookDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import gabia.cronMonitoring.entity.NoticeSubscription;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.WebhookSubscription;
import gabia.cronMonitoring.repository.NoticeSubscriptionRepository;
import gabia.cronMonitoring.repository.WebhookSubscriptionRepository;
import gabia.cronMonitoring.service.WebhookSubscriptionService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active:common")
@Transactional
@Rollback
class WebhookIntegrationTest {

    @Autowired
    WebhookSubscriptionService webhookSubscriptionService;

    @MockBean
    WebhookSubscriptionRepository webhookSubscriptionRepository;

    @MockBean
    NoticeSubscriptionRepository noticeSubscriptionRepository;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .build();
    }

    @Test
    public void 웹훅_목록_GET_성공() throws Exception {
        // Given
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

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .id(1L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId()))
            .thenReturn(Optional.of(noticeSubscription));
        // Then
        mockMvc.perform(
            get("/notifications/users/{userId}/crons/{cronJobId}/webhooks", user.getAccount(),
                cronJob.getId()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void 웹훅_추가_POST_성공() throws Exception {
        // Given
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

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .id(1L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();

        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();

        WebhookSubscription webhook = WebhookSubscription.builder()
            .id(1L)
            .noticeSubscription(noticeSubscription)
            .url("test")
            .endpoint(WebhookEndpoint.SLACK)
            .build();

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId()))
            .thenReturn(Optional.of(noticeSubscription));
        when(webhookSubscriptionRepository.save(any())).thenReturn(webhook);
        // Then
        mockMvc.perform(
            post("/notifications/users/{userId}/crons/{cronJobId}/webhooks", user.getAccount(),
                cronJob.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.url").value("test"))
            .andExpect(jsonPath("$.endPoint").value("SLACK"));
    }

    @Test
    public void 웹훅_수정_PATCH_성공() throws Exception {
        // Given
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

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .id(1L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();

        WebhookSubscription webhook = WebhookSubscription.builder()
            .id(1L)
            .noticeSubscription(noticeSubscription)
            .url("test")
            .endpoint(WebhookEndpoint.SLACK)
            .build();

        WebhookDTO request = WebhookDTO.builder()
            .url("test1")
            .endPoint(WebhookEndpoint.HIWORKS)
            .build();

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId()))
            .thenReturn(Optional.of(noticeSubscription));
        when(webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(WebhookEndpoint.HIWORKS,"test1", 1L))
            .thenReturn(Optional.empty());
        when(webhookSubscriptionRepository.findById(any())).thenReturn(Optional.of(webhook));
        when(webhookSubscriptionRepository.save(any())).thenReturn(webhook);
        // Then
        mockMvc.perform(
            patch("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", user.getAccount(),
                cronJob.getId(), webhook.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("test1"))
            .andExpect(jsonPath("$.endPoint").value("HIWORKS"));
    }

    @Test
    public void 웹훅_개별_삭제_DELETE_성공() throws Exception {
        // Given
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

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .id(1L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();
        WebhookSubscription webhook = WebhookSubscription.builder()
            .id(1L)
            .noticeSubscription(noticeSubscription)
            .url("test")
            .endpoint(WebhookEndpoint.SLACK)
            .build();
        // When
        when(webhookSubscriptionRepository.findById(webhook.getId())).thenReturn(Optional.of(webhook));
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", user.getAccount(),
                cronJob.getId(), webhook.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    public void 알림에_대한_웹훅_일괄_삭제_DELETE_성공() throws Exception {
        // Given
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

        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .id(1L)
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();
        // When
        when(noticeSubscriptionRepository.findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId()))
            .thenReturn(Optional.of(noticeSubscription));
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks", user.getAccount(),
                cronJob.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());
    }
}