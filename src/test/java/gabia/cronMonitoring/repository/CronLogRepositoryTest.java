package gabia.cronMonitoring.repository;

import gabia.cronMonitoring.entity.CronLog;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active:common")
@Transactional
public class CronLogRepositoryTest {

    @Autowired
    CronLogRepository cronLogRepository;

    @Test
    @Transactional
    public void InfluxDBTest() {

        //when
        List<CronLog> logs = cronLogRepository.findByTag("1");

        //then
        for (CronLog log : logs) {
            Assert.assertEquals("1",log.getCronProcess());
            Assert.assertEquals("test",log.getValue());

        }

    }



}