package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findById(String id);
}
