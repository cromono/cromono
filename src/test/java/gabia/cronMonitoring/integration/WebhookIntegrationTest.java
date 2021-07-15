package gabia.cronMonitoring.integration;

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
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.repository.NoticeSubscriptionRepository;
import gabia.cronMonitoring.repository.UserRepository;
import gabia.cronMonitoring.repository.WebhookSubscriptionRepository;
import gabia.cronMonitoring.service.WebhookSubscriptionService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active:common")
@Transactional
class WebhookIntegrationTest {

    @Autowired
    WebhookSubscriptionService webhookSubscriptionService;

    @Autowired
    WebhookSubscriptionRepository webhookSubscriptionRepository;

    @Autowired
    NoticeSubscriptionRepository noticeSubscriptionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CronJobRepositoryDataJpa cronJobRepository;

    @Autowired
    CronServerRepository cronServerRepository;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    String testStr = "test";
    String testEmail = "test@gabia.com";
    String testIp = "0.0.0.0";

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .build();
    }

    @Test
    public void 웹훅_목록_GET_성공() throws Exception {
        // Given
        User testUser = getTestUser();
        CronServer testServer = getTestServer();
        CronJob testJob = getTestJob(testServer);
        NoticeSubscription testNotice = getTestNotice(testUser, testJob);
        WebhookSubscription testWebhook = getTestWebhook(testNotice);
        // When
        // Then
        mockMvc.perform(
            get("/notifications/users/{userId}/crons/{cronJobId}/webhooks", testUser.getAccount(),
                testJob.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].url").value(testWebhook.getUrl()))
            .andExpect(jsonPath("$[0].endPoint").value(testWebhook.getEndpoint().toString()));
    }

    @Test
    public void 웹훅_추가_POST_성공() throws Exception {
        // Given
        User testUser = getTestUser();
        CronServer testServer = getTestServer();
        CronJob testJob = getTestJob(testServer);
        NoticeSubscription testNotice = getTestNotice(testUser, testJob);

        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();

        // When
        // Then
        mockMvc.perform(
            post("/notifications/users/{userId}/crons/{cronJobId}/webhooks", testUser.getAccount(),
                testJob.getId())
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
        User testUser = getTestUser();
        CronServer testServer = getTestServer();
        CronJob testJob = getTestJob(testServer);
        NoticeSubscription testNotice = getTestNotice(testUser, testJob);
        WebhookSubscription testWebhook = getTestWebhook(testNotice);

        WebhookDTO request = WebhookDTO.builder()
            .url("test1")
            .endPoint(WebhookEndpoint.HIWORKS)
            .build();

        // When
        // Then
        mockMvc.perform(
            patch("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}",
                testUser.getAccount(),
                testJob.getId(), testWebhook.getId())
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
        User testUser = getTestUser();
        CronServer testServer = getTestServer();
        CronJob testJob = getTestJob(testServer);
        NoticeSubscription testNotice = getTestNotice(testUser, testJob);
        WebhookSubscription testWebhook = getTestWebhook(testNotice);
        // When
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}",
                testUser.getAccount(),
                testJob.getId(), testWebhook.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    public void 알림에_대한_웹훅_일괄_삭제_DELETE_성공() throws Exception {
        // Given
        User testUser = getTestUser();
        CronServer testServer = getTestServer();
        CronJob testJob = getTestJob(testServer);
        NoticeSubscription testNotice = getTestNotice(testUser, testJob);
        // When
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks",
                testUser.getAccount(),
                testJob.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    private User getTestUser() {
        User user = User.builder()
            .account(testStr)
            .email(testEmail)
            .name(testStr)
            .password(testStr)
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();
        return userRepository.save(user);
    }

    private CronServer getTestServer() {
        CronServer cronServer = new CronServer(testIp);
        return cronServerRepository.save(cronServer);
    }

    private CronJob getTestJob(CronServer cronServer) {
        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setCronExpr(testStr);
        cronJob.setCronName(testStr);
        cronJob.setServer(cronServer);
        return cronJobRepository.save(cronJob);
    }

    private NoticeSubscription getTestNotice(User user, CronJob cronJob) {
        NoticeSubscription noticeSubscription = NoticeSubscription.builder()
            .createUser(user)
            .rcvUser(user)
            .cronJob(cronJob)
            .build();
        return noticeSubscriptionRepository.save(noticeSubscription);
    }

    private WebhookSubscription getTestWebhook(NoticeSubscription noticeSubscription) {
        WebhookSubscription webhookSubscription = WebhookSubscription.builder()
            .noticeSubscription(noticeSubscription)
            .endpoint(WebhookEndpoint.SLACK)
            .url(testStr)
            .build();
        return webhookSubscriptionRepository.save(webhookSubscription);
    }
}