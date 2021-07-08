package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.request.WebhookSubscriptionDTO;
import gabia.cronMonitoring.dto.response.WebhookDTO;
import gabia.cronMonitoring.entity.NoticeSubscription;
import gabia.cronMonitoring.entity.WebhookSubscription;
import gabia.cronMonitoring.exception.webhook.ExistingWebhookException;
import gabia.cronMonitoring.exception.webhook.NoticeSubscriptionNotFoundException;
import gabia.cronMonitoring.exception.webhook.WebhookNotFoundException;
import gabia.cronMonitoring.repository.NoticeSubscriptionRepository;
import gabia.cronMonitoring.repository.WebhookSubscriptionRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WebhookSubscriptionService {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final NoticeSubscriptionRepository noticeSubscriptionRepository;

    public List<WebhookDTO> getWebhooks(String userAccount, UUID cronJobId) {
        NoticeSubscription noticeSubscription = noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(userAccount, cronJobId).orElseThrow(() ->
                new NoticeSubscriptionNotFoundException("해당 사용자와 크론 잡에 대한 알림 구독 정보가 존재하지 않습니다."));
        List<WebhookDTO> response = webhookSubscriptionRepository
            .findAllByNoticeSubscriptionId(noticeSubscription.getId())
            .stream()
            .map(entity -> WebhookDTO.from(entity))
            .collect(Collectors.toList());

        return response;
    }

    public WebhookDTO addWebhook(String userAccount, UUID cronJobId,
        WebhookSubscriptionDTO request) {
        NoticeSubscription noticeSubscription = noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(userAccount, cronJobId).orElseThrow(() ->
                new NoticeSubscriptionNotFoundException("해당 사용자와 크론 잡에 대한 알림 구독 정보가 존재하지 않습니다."));
        webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(),
                request.getUrl(), noticeSubscription.getId()).ifPresent(none -> {
            throw new ExistingWebhookException("이미 등록된 웹훅입니다.");
        });
        WebhookSubscription newWebhook = WebhookSubscription.builder()
            .noticeSubscription(noticeSubscription)
            .endpoint(request.getEndPoint())
            .url(request.getUrl())
            .build();

        WebhookSubscription savedSubscription = webhookSubscriptionRepository.save(newWebhook);

        return WebhookDTO.from(savedSubscription);
    }

    public WebhookDTO updateWebhook(String userAccount, UUID cronJobId, Long id,
        WebhookSubscriptionDTO request) {
        NoticeSubscription noticeSubscription = noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(userAccount, cronJobId).orElseThrow(() ->
                new NoticeSubscriptionNotFoundException("해당 사용자와 크론 잡에 대한 알림 구독 정보가 존재하지 않습니다."));
        webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(),
                request.getUrl(), noticeSubscription.getId())
            .orElseThrow(() -> new WebhookNotFoundException("이미 등록된 웹훅입니다."));
        WebhookSubscription savedWebhook = webhookSubscriptionRepository
            .findById(id)
            .orElseThrow(() -> new WebhookNotFoundException("등록되지 않은 웹훅입니다."));
        savedWebhook.setEndpoint(request.getEndPoint());
        savedWebhook.setUrl(request.getUrl());

    }

    public void deleteWebhook() {

    }
}
