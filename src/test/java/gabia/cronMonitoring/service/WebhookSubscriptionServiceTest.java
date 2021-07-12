package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gabia.cronMonitoring.dto.request.WebhookDTO;
import gabia.cronMonitoring.dto.response.WebhookInfoDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import gabia.cronMonitoring.entity.NoticeSubscription;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.WebhookSubscription;
import gabia.cronMonitoring.exception.webhook.ExistingWebhookException;
import gabia.cronMonitoring.exception.webhook.NoticeSubscriptionNotFoundException;
import gabia.cronMonitoring.exception.webhook.WebhookNotFoundException;
import gabia.cronMonitoring.repository.NoticeSubscriptionRepository;
import gabia.cronMonitoring.repository.WebhookSubscriptionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebhookSubscriptionServiceTest {

    @InjectMocks
    WebhookSubscriptionService webhookSubscriptionService;
    @Mock
    WebhookSubscriptionRepository webhookSubscriptionRepository;
    @Mock
    NoticeSubscriptionRepository noticeSubscriptionRepository;

    @Test
    public void 웹훅_목록_조회_성공() throws Exception {
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

        WebhookSubscription webhookSubscription = WebhookSubscription.builder()
            .id(1L)
            .noticeSubscription(noticeSubscription)
            .endpoint(WebhookEndpoint.SLACK)
            .url("test")
            .build();

        List<WebhookSubscription> webhooks = new ArrayList<>();
        webhooks.add(webhookSubscription);

        List<WebhookInfoDTO> result = new ArrayList<>();
        result.add(WebhookInfoDTO.from(webhookSubscription));

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId()))
            .thenReturn(Optional.of(noticeSubscription));
        when(
            webhookSubscriptionRepository.findAllByNoticeSubscriptionId(noticeSubscription.getId()))
            .thenReturn(webhooks);
        List<WebhookInfoDTO> savedWebhooks = webhookSubscriptionService
            .getWebhooks(user.getAccount(), cronJob.getId());

        // Then
        Assertions.assertThat(savedWebhooks).isEqualTo(result);
    }

    @Test
    public void 존재하지_않는_알림에_대한_웹훅_목록_조회시_예외() throws Exception {
        // Given
        String userAccount = "test";
        UUID cronJobId = UUID.randomUUID();

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(any(), any()))
            .thenThrow(NoticeSubscriptionNotFoundException.class);

        // Then
        assertThrows(NoticeSubscriptionNotFoundException.class,
            () -> webhookSubscriptionService.getWebhooks(userAccount, cronJobId));
    }

    @Test
    public void 웹훅_등록_성공() throws Exception {
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

        WebhookSubscription webhookSubscription = WebhookSubscription.builder()
            .id(1L)
            .noticeSubscription(noticeSubscription)
            .endpoint(WebhookEndpoint.SLACK)
            .url("test")
            .build();

        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();

        WebhookInfoDTO response = WebhookInfoDTO.from(webhookSubscription);

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId())).thenReturn(
            Optional.of(noticeSubscription));
        when(webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(), request.getUrl(),
                noticeSubscription.getId()))
            .thenReturn(Optional.empty());
        when(webhookSubscriptionRepository.save(any()))
            .thenReturn(webhookSubscription);
        WebhookInfoDTO savedWebhook = webhookSubscriptionService
            .addWebhook(user.getAccount(), cronJob.getId(), request);

        // Then
        Assertions.assertThat(savedWebhook).isEqualTo(response);
    }

    @Test
    public void 존재하지_않는_알림에_대한_웹훅_등록시_예외() throws Exception {
        // Given
        String userAccount = "test";
        UUID cronJobId = UUID.randomUUID();
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(any(), any()))
            .thenThrow(NoticeSubscriptionNotFoundException.class);

        // Then
        assertThrows(NoticeSubscriptionNotFoundException.class,
            () -> webhookSubscriptionService.addWebhook(userAccount, cronJobId, request));
    }

    @Test
    public void 이미_등록된_알림_중복_등록시_예외() throws Exception {
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

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(any(), any()))
            .thenReturn(Optional.of(noticeSubscription));
        when(webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(),
                request.getUrl(), noticeSubscription.getId()))
            .thenThrow(ExistingWebhookException.class);

        // Then
        assertThrows(ExistingWebhookException.class,
            () -> webhookSubscriptionService
                .addWebhook(user.getAccount(), cronJob.getId(), request));
    }

    @Test
    public void 웹훅_정보_갱신시_성공() throws Exception {
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

        WebhookSubscription webhookSubscription = WebhookSubscription.builder()
            .id(1L)
            .noticeSubscription(noticeSubscription)
            .endpoint(WebhookEndpoint.SLACK)
            .url("test")
            .build();

        WebhookDTO request = WebhookDTO.builder()
            .url("new_url")
            .endPoint(WebhookEndpoint.HIWORKS)
            .build();

        WebhookInfoDTO response = WebhookInfoDTO.from(webhookSubscription);

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId()))
            .thenReturn(Optional.of(noticeSubscription));
        when(webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(),
                request.getUrl(), noticeSubscription.getId()))
            .thenReturn(Optional.empty());
        when(webhookSubscriptionRepository.findById(webhookSubscription.getId()))
            .thenReturn(Optional.of(webhookSubscription));
        WebhookInfoDTO updatedWebhook = webhookSubscriptionService
            .updateWebhook(user.getAccount(), cronJob.getId(), webhookSubscription.getId(),
                request);

        // Then
        Assertions.assertThat(updatedWebhook.getEndPoint()).isEqualTo(request.getEndPoint());
        Assertions.assertThat(updatedWebhook.getUrl()).isEqualTo(request.getUrl());
    }

    @Test
    public void 존재하지_않는_알림에_대한_웹훅_갱신시_예외() throws Exception {
        // Given
        String userAccount = "test";
        UUID cronJobId = UUID.randomUUID();
        Long webhookId = 1L;
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(any(), any()))
            .thenThrow(NoticeSubscriptionNotFoundException.class);

        // Then
        assertThrows(NoticeSubscriptionNotFoundException.class,
            () -> webhookSubscriptionService
                .updateWebhook(userAccount, cronJobId, webhookId, request));
    }

    @Test
    public void 이미_등록된_알림으로_갱신시_예외() throws Exception {
        // Given
        Long webhookId = 1L;

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

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(any(), any()))
            .thenReturn(Optional.of(noticeSubscription));
        when(webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(),
                request.getUrl(), noticeSubscription.getId()))
            .thenThrow(ExistingWebhookException.class);

        // Then
        assertThrows(ExistingWebhookException.class,
            () -> webhookSubscriptionService
                .updateWebhook(user.getAccount(), cronJob.getId(), webhookId, request));
    }

    @Test
    public void 미등록_웹훅_갱신시_예외() throws Exception {
        // Given
        Long webhookId = 1L;

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

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(any(), any()))
            .thenReturn(Optional.of(noticeSubscription));
        when(webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(),
                request.getUrl(), noticeSubscription.getId()))
            .thenReturn(Optional.empty());
        when(webhookSubscriptionRepository.findById(any()))
            .thenThrow(WebhookNotFoundException.class);

        // Then
        assertThrows(WebhookNotFoundException.class,
            () -> webhookSubscriptionService
                .updateWebhook(user.getAccount(), cronJob.getId(), webhookId, request));
    }

    @Test
    public void 웹훅_개별_삭제_성공() throws Exception {
        // Given
        Long webhookId = 1L;

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

        WebhookSubscription webhookSubscription = WebhookSubscription.builder()
            .id(1L)
            .noticeSubscription(noticeSubscription)
            .endpoint(WebhookEndpoint.SLACK)
            .url("test")
            .build();

        // When
        when(webhookSubscriptionRepository
            .findById(webhookId)).thenReturn(Optional.of(webhookSubscription));

        // Then
        assertDoesNotThrow(() -> webhookSubscriptionService.deleteWebhookById(webhookId));
    }

    @Test
    public void 미등록_웹훅_삭제시_예외() throws Exception {
        // Given
        Long webhookId = 1L;

        // When
        when(webhookSubscriptionRepository
            .findById(webhookId)).thenThrow(WebhookNotFoundException.class);
        // Then
        assertThrows(WebhookNotFoundException.class,
            () -> webhookSubscriptionService.deleteWebhookById(webhookId));
    }

    @Test
    public void 웹훅_일괄_삭제_성공() throws Exception {
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
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId())).thenReturn(
            Optional.of(noticeSubscription));
        // Then
        assertDoesNotThrow(
            () -> webhookSubscriptionService.deleteWebhooks(user.getAccount(), cronJob.getId()));
    }

    @Test
    public void 존재하지_않는_알림에_대한_웹훅_일괄_삭제시_예외() throws Exception {
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

        // When
        when(noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(user.getAccount(), cronJob.getId())).thenThrow(
            NoticeSubscriptionNotFoundException.class);
        // Then
        assertThrows(NoticeSubscriptionNotFoundException.class,
            () -> webhookSubscriptionService.deleteWebhooks(user.getAccount(), cronJob.getId()));
    }
}