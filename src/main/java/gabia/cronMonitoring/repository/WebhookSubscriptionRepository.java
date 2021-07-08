package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import gabia.cronMonitoring.entity.WebhookSubscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {

    List<WebhookSubscription> findAllByNoticeSubscriptionId(long noticeSubscriptionId);

    Optional<WebhookSubscription> findByEndpointAndUrlAndNoticeSubscriptionId(WebhookEndpoint endpoint, String url, long noticeSubscriptionId);
}
