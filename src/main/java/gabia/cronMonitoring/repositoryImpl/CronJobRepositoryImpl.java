package gabia.cronMonitoring.repositoryImpl;

import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronJobRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CronJobRepositoryImpl implements CronJobRepository {

    private final EntityManager em;

    @Override
    public Optional<UUID> save(CronJob cronjob) {
        em.persist(cronjob);
        return Optional.of(cronjob.getId());
    }

    @Override
    public Optional<CronJob> findOne(UUID id) {
        return Optional.ofNullable(em.find(CronJob.class, id));
    }

    @Override
    public List<CronJob> findByServer(String cronServerIp) {
        return em.createQuery("select cj from CronJob cj where cj.server.ip =: serverIp", CronJob.class)
            .setParameter("serverIp",cronServerIp).getResultList();
    }

    @Override
    public List<CronJob> findAll() {
        return em.createQuery("select cj from CronJob cj", CronJob.class).getResultList();
    }

    @Override
    public Optional<UUID> deleteById(UUID id) {
        CronJob targetCronJob = em.find(CronJob.class, id);
        em.remove(targetCronJob);
        return Optional.ofNullable(targetCronJob.getId());
    }

    @Override
    public Optional<UUID> delete(CronJob cronJob) {
        UUID targetId = cronJob.getId();
        em.remove(cronJob);
        return Optional.ofNullable(targetId);
    }


}
