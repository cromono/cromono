package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronLog;
import java.util.List;

public interface CronLogRepository {

    List<CronLog> findByTag(String cronProcess);

}
