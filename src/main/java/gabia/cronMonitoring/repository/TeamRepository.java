package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByAccount(String account);
    void deleteByAccount(String account);
}
