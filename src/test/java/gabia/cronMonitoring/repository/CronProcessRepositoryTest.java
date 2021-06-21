package gabia.cronMonitoring.repository;

import static org.junit.jupiter.api.Assertions.*;

import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.entity.CronServer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class CronProcessRepositoryTest {

    @Autowired
    CronProcessRepository cronProcessRepository;

    @Autowired
    CronJobRepository cronJobRepository;

    @Autowired
    CronServerRepository cronServerRepository;

    @Test
    void findAllByCronJob_Id() {
        //given
        CronServer cronServer = new CronServer();
        cronServer.setIp("0.0.0.0");
        cronServerRepository.save(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        CronJob cronJob = new CronJob();
        cronJob.setCronName("test");
        cronJob.setCronExpr("test");
        cronJob.setServer(cronServer);
        CronJob cronJob1 = cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .cronJob(cronJob)
            .startTime(timestamp)
            .build();
        cronProcessRepository.save(cronProcess);

        CronProcess cronProcess2 = CronProcess.builder()
            .pid("12")
            .cronJob(cronJob)
            .startTime(timestamp)
            .build();
        cronProcessRepository.save(cronProcess2);

        //when
        List<CronProcess> allByCronJobId = cronProcessRepository
            .findAllByCronJob_Id(cronJob1.getId());

        //then
        Assertions.assertThat(cronProcess.getPid()).isEqualTo(allByCronJobId.get(0).getPid());
        Assertions.assertThat(cronProcess2.getPid()).isEqualTo(allByCronJobId.get(1).getPid());

    }

    @Test
    void findByPid() {
        //given
        CronServer cronServer = new CronServer();
        cronServer.setIp("0.0.0.0");
        cronServerRepository.save(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        CronJob cronJob = new CronJob();
        cronJob.setCronName("test");
        cronJob.setCronExpr("test");
        cronJob.setServer(cronServer);
        cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .cronJob(cronJob)
            .startTime(timestamp)
            .build();
        cronProcessRepository.save(cronProcess);

        //when
        CronProcess cronProcess1 = cronProcessRepository.findByPid("1").get();

        //then
        Assertions.assertThat(cronProcess.getPid()).isEqualTo(cronProcess1.getPid());

    }

    @Test
    void save() {
        //given
        CronServer cronServer = new CronServer();
        cronServer.setIp("0.0.0.0");
        cronServerRepository.save(cronServer);

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        CronJob cronJob = new CronJob();
        cronJob.setCronName("test");
        cronJob.setCronExpr("test");
        cronJob.setServer(cronServer);
        cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .cronJob(cronJob)
            .startTime(timestamp)
            .build();

        //when
        CronProcess cronProcess1 = cronProcessRepository.save(cronProcess);

        //then
        Assertions.assertThat(cronProcess.getPid()).isEqualTo(cronProcess1.getPid());


    }
}