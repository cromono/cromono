package gabia.cronMonitoring.repositoryImpl;

import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.repository.CronJobRepository;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CronJobRepositoryImpl implements CronJobRepository {

    private final EntityManager em;

    @Override
    public UUID save(CronJob cronjob) {
        em.persist(cronjob);
        return cronjob.getId();
    }

    @Override
    public CronJob findOne(UUID id) {
        return em.find(CronJob.class, id);
    }

    @Override
    public List<CronJob> findAll() {
        return em.createQuery("select cj from CronJob cj", CronJob.class).getResultList();
    }

    @Override
    public UUID deleteById(UUID id) {
        CronJob targetCronJob = em.find(CronJob.class, id);
        em.remove(targetCronJob);
        return targetCronJob.getId();
    }

    @Override
    public UUID delete(CronJob cronJob) {
        UUID targetId = cronJob.getId();
        em.remove(cronJob);
        return targetId;
    }


}
