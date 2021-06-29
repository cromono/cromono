package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    Optional<TeamUser> findByTeamAccountAndUserAccount(String teamAccount, String userAccount);

    void deleteByTeamAccount(String teamAccount);

}
