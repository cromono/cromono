package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CronJobRepository {

    CronJob save(CronJob cronJob);

    Optional<CronJob> findById(UUID id);

    List<CronJob> findByServer(String cronServerIp);

    List<CronJob> findAll();

    Optional<UUID> deleteById(UUID id);

    Optional<UUID> delete(CronJob cronJob);


}
