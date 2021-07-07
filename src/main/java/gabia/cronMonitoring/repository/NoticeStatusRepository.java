package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.NoticeStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeStatusRepository extends JpaRepository<NoticeStatus, Long> {

    Optional<NoticeStatus> findByRcvUserId(String account);

    Optional<NoticeStatus> findByRcvUserIdAndNoticeId(String account, Long notId);

}
