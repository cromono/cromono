package gabia.cronMonitoring.repositoryImpl;

import static org.assertj.core.api.Assertions.assertThat;

import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronServerRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CronServerRepositoryImplTest {

    @Autowired
    CronServerRepository cronServerRepository;

    @Autowired
    EntityManager em;

    @Test
    public void save() {
        // Given
        CronServer cronServer = new CronServer("1:1:1:1");
        // When
        CronServer savedServer = cronServerRepository.save(cronServer);
        // Then
        assertThat(savedServer).isEqualTo(cronServer);
    }

    @Test
    public void findByIp() {
        // Given
        CronServer cronServer = new CronServer("1:1:1:1");
        em.persist(cronServer);
        // When
        CronServer findServer = cronServerRepository.findByIp("1:1:1:1").get();
        // Then
        assertThat(findServer).isEqualTo(cronServer);
    }

    @Test
    public void findAll() {
        // Given
        CronServer server1 = new CronServer("1:1:1:1");
        em.persist(server1);

        CronServer server2 = new CronServer("1:1:1:2");
        em.persist(server2);
        // When
        List<CronServer> result = cronServerRepository.findAll();
        // Then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void delete() {
        // Given
        CronServer cronServer = new CronServer("1:1:1:1");
        em.persist(cronServer);
        // When
        cronServerRepository.delete(cronServer);
        // Then
        assertThat(cronServerRepository.findByIp("1:1:1:1")).isEmpty();
    }
}