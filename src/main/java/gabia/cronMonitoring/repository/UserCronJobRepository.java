package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.UserCronJob;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCronJobRepository extends JpaRepository<UserCronJob, Long> {

    List<UserCronJob> findByUserAccount(String account);

    void deleteByCronJobIdAndUserAccount(UUID cronJobId, String account);

    UserCronJob save(UserCronJob userCronJob);
}
