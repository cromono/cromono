package gabia.cronMonitoring.repositoryImpl;

import static org.assertj.core.api.Assertions.*;

import gabia.cronMonitoring.entity.CronServer;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CronServerRepositoryImplTest {

    @Autowired
    CronServerRepositoryImpl cronServerRepository;

    @Test
    @Transactional
    public void save() {
        // Given
        CronServer cronServer = new CronServer("1:1:1:1");
        // When
        CronServer savedServer = cronServerRepository.save(cronServer);
        // Then
        assertThat(savedServer).isEqualTo(cronServer);
    }

    @Test
    @Transactional
    public void findByIp() {
        // Given
        CronServer cronServer = new CronServer("1:1:1:1");
        CronServer saveServer = cronServerRepository.save(cronServer);
        // When
        CronServer findServer = cronServerRepository.findByIp("1:1:1:1").get();
        // Then
        assertThat(findServer).isEqualTo(saveServer);
    }

    @Test
    @Transactional
    public void findAll() {
        // Given
        CronServer server1 = new CronServer("1:1:1:1");
        cronServerRepository.save(server1);

        CronServer server2 = new CronServer("1:1:1:2");
        cronServerRepository.save(server2);
        // When
        List<CronServer> result = cronServerRepository.findAll();
        // Then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void delete() {
        // Given
        CronServer cronServer = new CronServer("1:1:1:1");
        cronServerRepository.save(cronServer);
        // When
        cronServerRepository.delete(cronServer);
        // Then
        assertThat(cronServerRepository.findByIp("1:1:1:1")).isEmpty();
    }
}