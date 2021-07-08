package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamUser;
import gabia.cronMonitoring.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    Optional<TeamUser> findByTeamAccountAndUserAccount(String teamAccount, String userAccount);

    void deleteByTeamAccount(String teamAccount);

    List<TeamUser> findByTeamAccount(String teamAccount);

    void deleteByTeamAccountAndUserAccount(String teamAccount, String userAccount);
}
