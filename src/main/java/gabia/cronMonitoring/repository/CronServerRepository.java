package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronServer;
import java.util.List;
import java.util.Optional;

public interface CronServerRepository {
    public CronServer save(CronServer cronServer);
    public Optional<CronServer> findByIp(String ip);
    public List<CronServer> findAll();
    public void delete(CronServer cronServer);
}
