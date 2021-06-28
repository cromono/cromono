package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.TeamCronJob;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamCronJobRepository extends JpaRepository<TeamCronJob, Long> {

    List<TeamCronJob> findByTeamAccount(String account);

    TeamCronJob save(TeamCronJob teamCronJob);

    void deleteByCronJobIdAndTeamAccount(UUID cronJobId, String account);
}
