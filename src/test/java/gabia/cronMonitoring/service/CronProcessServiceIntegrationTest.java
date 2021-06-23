package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import gabia.cronMonitoring.dto.CronProcessDto.Request;
import gabia.cronMonitoring.dto.CronProcessDto.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.process.CronProcessNotFoundException;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CronProcessServiceIntegrationTest {

    @Autowired
    CronProcessService cronProcessService;

    @Autowired
    CronProcessRepository cronProcessRepository;

    @Autowired
    CronJobRepository cronJobRepository;

    @Autowired
    CronServerRepository cronServerRepository;

    @Test
    public void 크론_프로세스_목록_조회() {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        CronJob cronJob1 = cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .startTime(timestamp)
            .cronJob(cronJob)
            .build();
        CronProcess cronProcess2 = CronProcess.builder()
            .pid("2")
            .startTime(timestamp)
            .cronJob(cronJob)
            .build();

        CronProcess savedCronProcess1 = cronProcessRepository.save(cronProcess);
        CronProcess savedCronProcess2 = cronProcessRepository.save(cronProcess2);

        //when
        List<Response> allCronProcess = cronProcessService.findAllCronProcess(cronJob1.getId());

        //then
        Assertions.assertEquals(savedCronProcess1.getPid(), allCronProcess.get(0).getPid());
        Assertions.assertEquals(savedCronProcess2.getPid(), allCronProcess.get(1).getPid());

    }

    @Test
    public void 크론_프로세스_생성() {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        CronJob cronJob1 = cronJobRepository.save(cronJob);

        //when
        Request request = new Request();
        request.setStartTime(timestamp);
        request.setPid("1");
        Response cronProcess = cronProcessService.makeCronProcess(cronJob1.getId(), request);

        //then
        Assertions.assertEquals(cronProcess.getPid(), "1");
        Assertions.assertEquals(cronProcess.getStartTime(), timestamp);
    }

    @Test
    public void 크론_프로세스_생성_크론잡이_없는_경우() throws Exception {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //then
        assertThrows(CronJobNotFoundException.class, () -> {
            //when
            Request request = new Request();
            request.setStartTime(timestamp);
            request.setPid("1");
            cronProcessService.makeCronProcess(UUID.randomUUID(), request);
        });
    }

    @Test
    public void 크론_프로세스_조회() {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .startTime(timestamp)
            .endTime(timestamp)
            .cronJob(cronJob)
            .build();
        CronProcess savedCronProcess = cronProcessRepository.save(cronProcess);

        //when
        Response findCronProcess = cronProcessService.findCronProcess("1");

        //then
        Assertions.assertEquals(findCronProcess.getPid(), savedCronProcess.getPid());
        Assertions.assertEquals(findCronProcess.getStartTime(), savedCronProcess.getStartTime());
        Assertions
            .assertEquals(findCronProcess.getCronJobId(), savedCronProcess.getCronJob().getId());

    }

    @Test
    public void 크론_프로세스_조회_크론프로세스가_없는_경우() throws Exception {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .startTime(timestamp)
            .endTime(timestamp)
            .cronJob(cronJob)
            .build();
        cronProcessRepository.save(cronProcess);

        //then
        assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            cronProcessService.findCronProcess("2");
        });
    }

    @Test
    public void 크론_프로세스_변경() {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .startTime(timestamp)
            .endTime(timestamp)
            .cronJob(cronJob)
            .build();
        cronProcessRepository.save(cronProcess);

        //when
        Request request = new Request();
        request.setPid("1");
        request.setEndTime(timestamp);
        Response response = cronProcessService.changeCronProcess("1", request);

        //then
        Assertions.assertEquals(timestamp, response.getEndTime());

    }

    @Test
    public void 크론_프로세스_변경_크론프로세스가_없는_경우() throws Exception {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        CronProcess cronProcess = CronProcess.builder()
            .pid("1")
            .startTime(timestamp)
            .endTime(timestamp)
            .cronJob(cronJob)
            .build();
        cronProcessRepository.save(cronProcess);

        //then
        assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            Request request = new Request();
            request.setPid("1");
            request.setEndTime(timestamp);
            cronProcessService.changeCronProcess("2", request);
        });

    }
}