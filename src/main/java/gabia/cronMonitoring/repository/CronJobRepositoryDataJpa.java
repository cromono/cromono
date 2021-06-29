package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CronJobRepositoryDataJpa extends JpaRepository<CronJob, UUID> {

}
