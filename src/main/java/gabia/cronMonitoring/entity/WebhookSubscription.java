package gabia.cronMonitoring.entity;


import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "webhook_subscription")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webhook_subscription_id")
    private Long id;

    @NotNull
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_subscription_id", referencedColumnName = "notice_subscription_id")
    private NoticeSubscription noticeSubscription;

    @Column(name = "endpoint")
    @NotNull
    @NonNull
    @Enumerated(EnumType.STRING)
    private WebhookEndpoint endpoint;

    @Column(name = "url")
    @NotNull
    @NonNull
    private String url;
}
