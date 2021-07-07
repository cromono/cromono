package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.Notice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findById(Long notId);

    List<Notice> findByCronJobIdIn(List<UUID> cronJobIdList);
}
