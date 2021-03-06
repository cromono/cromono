package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.*;

import gabia.cronMonitoring.dto.CronLogDto;
import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.dto.CronProcessDto.Request;
import gabia.cronMonitoring.dto.CronProcessDto.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronLog;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.exception.cron.process.CronProcessNotFoundException;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronLogRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
class CronProcessServiceTest {

    @Mock
    private CronProcessRepository cronProcessRepository;

    @Mock
    private CronJobRepository cronJobRepository;

    @Mock
    private CronLogRepository cronLogRepository;

    @InjectMocks
    private CronProcessService cronProcessService;

    @Test
    void findAllProcess_해당_잡의_프로세스가_존재하는_경우() {
        //given
        openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

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

        List<CronProcess> cronProcessList = new ArrayList<>();
        cronProcessList.add(cronProcess);
        cronProcessList.add(cronProcess2);
        given(cronProcessRepository.findAllByCronJob_Id(
            UUID.fromString("123e4567-e89b-12d3-a456-556642440000")))
            .willReturn(new ArrayList<CronProcess>(cronProcessList));

        //when
        final List<CronProcessDto.Response> result = cronProcessService
            .findAllCronProcess(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //then
        Assertions.assertThat(result.get(0).getPid()).isEqualTo("1");
        Assertions.assertThat(result.get(0).getStartTime()).isEqualTo(timestamp);
        Assertions.assertThat(result.get(1).getPid()).isEqualTo("2");
        Assertions.assertThat(result.get(1).getStartTime()).isEqualTo(timestamp);


    }

    @Test
    void findAllProcess_해당_잡의_프로세스가_존재하지_않는_경우() {
        //given
        openMocks(this);
        given(cronProcessRepository.findAllByCronJob_Id(
            UUID.fromString("123e4567-e89b-12d3-a456-556642440000")))
            .willReturn(new ArrayList<>());

        //when
        final List<CronProcessDto.Response> result = cronProcessService
            .findAllCronProcess(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //then
        Assertions.assertThat(result.isEmpty()).isEqualTo(true);
    }

    @Test
    void makeProcess_크론잡이_존재히는_경우() {
        //given
        openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        given(cronProcessRepository.save(any(CronProcess.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());
        Optional<CronJob> testCronJob = Optional.of(new CronJob());
        testCronJob.get().setId(UUID.fromString("123e4567-e89b-12d3-a456-556642440001"));
        testCronJob.get().setCronExpr("test");
        testCronJob.get().setCronName("test");
        given(cronJobRepository.findById(UUID.fromString("123e4567-e89b-12d3-a456-556642440001")))
            .willReturn(testCronJob);

        //when
        CronProcessDto.Request request = new Request();
        request.setPid("28");
        request.setStartTime(timestamp);
        Response response = cronProcessService
            .makeCronProcess(UUID.fromString("123e4567-e89b-12d3-a456-556642440001"),
                request);

        //then
        Assertions.assertThat(response.getPid()).isEqualTo("28");
        Assertions.assertThat(response.getStartTime()).isEqualTo(timestamp);
    }

    @Test
    void makeProcess_크론잡이_존재하지_않는_경우() {

        openMocks(this);
        given(
            cronJobRepository.findById(UUID.fromString("123e4567-e89b-12d3-a456-556642440001")))
            .willReturn(Optional.empty());

        //then
        assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            cronProcessService.findCronProcess("28");
        });
    }

    @Test
    void findProcess_크론_프로세스가_존재하는_경우() {
        //given
        openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronJob cronJob = new CronJob();
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        Optional<CronProcess> cronProcess = Optional
            .of(CronProcess.builder().id(1L).pid("28").startTime(timestamp).cronJob(cronJob)
                .build());
        given(cronProcessRepository.findByPid("28")).willReturn(cronProcess);

        //when
        Response response = cronProcessService.findCronProcess("28");

        //then
        Assertions.assertThat(response.getPid()).isEqualTo("28");
        Assertions.assertThat(response.getStartTime()).isEqualTo(timestamp);
    }

    @Test
    void findProcess_크론_프로세스가_존재하지_않는_경우() {
        //given
        openMocks(this);
        given(cronProcessRepository.findByPid("28")).willReturn(Optional.empty());

        //then
        assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            cronProcessService.findCronProcess("28");
        });

    }


    @Test
    void changeCronProcess_크론프로세스가_존재하는_경우() {
        //given
        openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        given(cronProcessRepository.save(any(CronProcess.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        CronJob cronJob = new CronJob();
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        Optional<CronProcess> testCronProcess = Optional
            .of(CronProcess.builder().id(1L).pid("28").startTime(timestamp).cronJob(cronJob)
                .build());
        given(cronProcessRepository.findByPid("28")).willReturn(testCronProcess);

        //when
        CronProcessDto.Request request = new CronProcessDto.Request();
        request.setEndTime(timestamp);
        Response response = cronProcessService.changeCronProcess("28", request);

        //then
        Assertions.assertThat(response.getEndTime()).isEqualTo(timestamp);
    }

    @Test
    void changeCronProcess_크론프로세스가_존재하지_않는_경우() {
        //given
        openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        given(cronProcessRepository.findByPid("28")).willReturn(Optional.empty());

        //then
        assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            CronProcessDto.Request request = new CronProcessDto.Request();
            request.setEndTime(timestamp);
            cronProcessService.changeCronProcess("2", request);
        });

    }

    @Test
    void findCronLogs() {
        //given
        openMocks(this);
        List<CronLog> responses = new ArrayList<>();
        CronLog cronLog = new CronLog(Instant.now(), "1", Instant.now(), Instant.now(), "test log");
        CronLog cronLog2 = new CronLog(Instant.now(), "1", Instant.now(), Instant.now(), "test log1");


        responses.add(cronLog);
        responses.add(cronLog2);

        given(cronLogRepository.findByTag("1")).willReturn(responses);

        //when
        List<CronLogDto.Response> cronLogs = cronProcessService.findCronLogs("1");

        //then
        Assertions.assertThat(cronLogs.get(0).getCronProcess()).isEqualTo("1");
        Assertions.assertThat(cronLogs.get(0).getValue()).isEqualTo(cronLog.getValue());
        Assertions.assertThat(cronLogs.get(0).getStart()).isEqualTo(cronLog.getStart());
        Assertions.assertThat(cronLogs.get(0).getStop()).isEqualTo(cronLog.getStop());

        Assertions.assertThat(cronLogs.get(1).getCronProcess()).isEqualTo("1");
        Assertions.assertThat(cronLogs.get(1).getValue()).isEqualTo(cronLog2.getValue());
        Assertions.assertThat(cronLogs.get(1).getStart()).isEqualTo(cronLog2.getStart());
        Assertions.assertThat(cronLogs.get(1).getStop()).isEqualTo(cronLog2.getStop());



    }
}