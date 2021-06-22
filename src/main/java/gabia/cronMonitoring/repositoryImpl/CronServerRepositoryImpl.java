package gabia.cronMonitoring.repositoryImpl;

import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronServerRepository;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CronServerRepositoryImpl implements CronServerRepository {

    private final EntityManager em;

    @Override
    public CronServer save(CronServer cronServer) {
        if (cronServer.getId() == null) {
            em.persist(cronServer);
            return cronServer;
        }
        else {
            return em.merge(cronServer);
        }
    }

    @Override
    public Optional<CronServer> findByIp(String ip) {
        return em.createQuery("select s from CronServer s where s.ip = :ip")
            .setParameter("ip", ip)
            .setMaxResults(1)
            .getResultList().stream().findFirst();
    }

    @Override
    public List<CronServer> findAll() {
        return em.createQuery("select s from CronServer s")
            .getResultList();
    }

    @Override
    public void delete(CronServer cronServer) {
        em.remove(cronServer);
    }
}
