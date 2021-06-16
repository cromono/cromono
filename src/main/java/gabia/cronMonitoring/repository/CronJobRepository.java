package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronJob;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CronJobRepository {

    Optional<UUID> save(CronJob cronJob);

    Optional<CronJob> findOne(UUID id);

    List<CronJob> findAll();

    Optional<UUID> deleteById(UUID id);

    Optional<UUID> delete(CronJob cronJob);


}
