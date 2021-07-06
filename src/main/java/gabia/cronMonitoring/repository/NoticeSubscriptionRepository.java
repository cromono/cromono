package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.NoticeSubscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeSubscriptionRepository extends JpaRepository<NoticeSubscription, Long> {

    List<NoticeSubscription> findByRcvUserAccount(String account);

    void deleteByRcvUserAccountAndCronJobId(String account, UUID cronJobId);

    Optional<NoticeSubscription> findByRcvUserAccountAndCronJobId(String account, UUID cronJobId);

    @Query("select n.cronJob.id from NoticeSubscription n where n.rcvUser.account = :rcvUser")
    List<UUID> findCronJobIdByRcvUserAccount(@Param("rcvUser") String account);

}
