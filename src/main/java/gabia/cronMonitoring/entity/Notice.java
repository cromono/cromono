package gabia.cronMonitoring.entity;

import gabia.cronMonitoring.entity.Enum.NoticeType;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "not_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User receiveUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User createUser;

    @Enumerated(EnumType.STRING)
    @NotNull
    private NoticeType noticeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private CronJob cronJob;

    @NotNull
    private String noticeMessage;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticeCreateDatetime;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date noticeReadDatetime;

}
