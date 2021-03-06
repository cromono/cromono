package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccount(String account);

    Optional<User> findByEmail(String email);

    void deleteByAccount(String account);
}
