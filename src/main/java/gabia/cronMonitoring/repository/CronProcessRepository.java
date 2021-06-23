package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronProcess;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface CronProcessRepository {

    List<CronProcess> findAllByCronJob_Id(UUID cronJobId);

    Optional<CronProcess> findByPid(String pid);

    CronProcess save(CronProcess cronProcess);

}
