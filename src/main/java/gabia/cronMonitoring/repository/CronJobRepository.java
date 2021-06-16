package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronJob;
import java.util.List;
import java.util.UUID;

public interface CronJobRepository {

    UUID save(CronJob cronJob);

    CronJob findOne(UUID id);

    List<CronJob> findAll();

    UUID deleteById(UUID id);

    UUID delete(CronJob cronJob);


}
