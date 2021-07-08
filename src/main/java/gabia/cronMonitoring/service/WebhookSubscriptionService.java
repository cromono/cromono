package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.request.WebhookDTO;
import gabia.cronMonitoring.dto.response.WebhookInfoDTO;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 웹훅과 관련된 서비스를 처리하는 클래스입니다. 
* @author : 김기정(Luke)
**/
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WebhookSubscriptionService {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final NoticeSubscriptionRepository noticeSubscriptionRepository;

    /**
     * 알림에 대해 등록된 웹훅 목록 조회
     * @param userAccount 사용자 ID
     * @param cronJobId 크론잡 ID
     * @return 웹훅 정보 DTO 목록
     */
    public List<WebhookInfoDTO> getWebhooks(String userAccount, UUID cronJobId) {
        NoticeSubscription noticeSubscription = noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(userAccount, cronJobId).orElseThrow(() ->
                new NoticeSubscriptionNotFoundException("해당 사용자와 크론 잡에 대한 알림 구독 정보가 존재하지 않습니다."));
        List<WebhookInfoDTO> response = webhookSubscriptionRepository
            .findAllByNoticeSubscriptionId(noticeSubscription.getId())
            .stream()
            .map(entity -> WebhookInfoDTO.from(entity))
            .collect(Collectors.toList());

        return response;
    }

    /**
     * 웹훅 추가
     * @param userAccount 사용자 ID
     * @param cronJobId 크론잡 ID
     * @param request 웹훅 DTO
     * @return 추가된 웹훅의 정보 DTO
     */
    public WebhookInfoDTO addWebhook(String userAccount, UUID cronJobId,
        WebhookDTO request) {

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

        return WebhookInfoDTO.from(savedSubscription);
    }

    /**
     * 웹훅 갱신
     * @param userAccount 사용자 ID
     * @param cronJobId 크론잡 ID
     * @param id 웹훅 ID
     * @param request 웹훅 DTO
     * @return 갱신된 웹훅의 정보 DTO
     */
    public WebhookInfoDTO updateWebhook(String userAccount, UUID cronJobId, Long id,
        WebhookDTO request) {

        NoticeSubscription noticeSubscription = noticeSubscriptionRepository
            .findByRcvUserAccountAndCronJobId(userAccount, cronJobId).orElseThrow(() ->
                new NoticeSubscriptionNotFoundException("해당 사용자와 크론 잡에 대한 알림 구독 정보가 존재하지 않습니다."));
        webhookSubscriptionRepository
            .findByEndpointAndUrlAndNoticeSubscriptionId(request.getEndPoint(),
                request.getUrl(), noticeSubscription.getId())
            .orElseThrow(() -> new ExistingWebhookException("이미 등록된 웹훅입니다."));
        WebhookSubscription savedWebhook = webhookSubscriptionRepository
            .findById(id)
            .orElseThrow(() -> new WebhookNotFoundException("등록되지 않은 웹훅입니다."));
        savedWebhook.setEndpoint(request.getEndPoint());
        savedWebhook.setUrl(request.getUrl());
        webhookSubscriptionRepository.save(savedWebhook);

        return WebhookInfoDTO.from(savedWebhook);
    }

    /**
     * 웹훅 삭제
     * @param id 삭제할 웹훅의 ID
     */
    public void deleteWebhook(Long id) {
        WebhookSubscription savedWebhook = webhookSubscriptionRepository
            .findById(id)
            .orElseThrow(() -> new WebhookNotFoundException("등록되지 않은 웹훅입니다."));
        webhookSubscriptionRepository.delete(savedWebhook);
    }
}
