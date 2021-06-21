package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronProcess;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CronProcessRepositoryImpl implements CronProcessRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<CronProcess> findByPid(String pid) {
        CronProcess cronProcess = em
            .createQuery("select cp from CronProcess cp where cp.pid = :pid", CronProcess.class)
            .setParameter("pid", pid).getSingleResult();
        return Optional.ofNullable(cronProcess);
    }

    @Override
    public List<CronProcess> findAllByCronJob_Id(UUID cronJobId) {
        return em.createQuery("select cp from CronProcess cp where cp.cronJob.id = :cronJobId",
            CronProcess.class).setParameter("cronJobId", cronJobId).getResultList();
    }

    @Override
    public CronProcess save(CronProcess cronProcess) {

        if (cronProcess.getId() == null) {
            em.persist(cronProcess);
        } else {
            em.merge(cronProcess);
        }
        return cronProcess;
    }
}