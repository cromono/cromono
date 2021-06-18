package gabia.cronMonitoring.service;

import static org.mockito.ArgumentMatchers.any;

import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.dto.CronProcessDto.Request;
import gabia.cronMonitoring.dto.CronProcessDto.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.exception.CronJobNotFoundException;
import gabia.cronMonitoring.exception.CronProcessNotFoundException;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.swing.text.html.Option;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito.*;
import org.mockito.BDDMockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
class CronProcessServiceTest {

    @Mock
    private CronProcessRepository cronProcessRepository;

    @Mock
    private CronJobRepository cronJobRepository;

    @Mock
    private CronServerRepository cronServerRepository;

    @InjectMocks
    private CronProcessService cronProcessService;

    @Test
    void findAllProcess_해당_잡의_프로세스가_존재하는_경우() {
        // 해당하는 잡의 프로세스가 존재하는 경우
        //given
        MockitoAnnotations.openMocks(this);
        CronServer cronServer = new CronServer();
        CronJob cronJob = new CronJob();
        CronProcess cronProcess = new CronProcess();
        CronProcess cronProcess2 = new CronProcess();

        cronServer.setIp("0.0.0.0");

        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        cronProcess.setPid("1");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        cronProcess.setStartTime(timestamp);
        cronProcess.setCronJob(cronJob);

        cronProcess2.setPid("2");
        cronProcess2.setStartTime(timestamp);
        cronProcess2.setCronJob(cronJob);

        List<CronProcess> cronProcessList = new ArrayList<>();
        cronProcessList.add(cronProcess);
        cronProcessList.add(cronProcess2);
        BDDMockito.given(cronProcessRepository.findAllByCronJob_Id(
            UUID.fromString("123e4567-e89b-12d3-a456-556642440000")))
            .willReturn(new ArrayList<CronProcess>(cronProcessList));

        //when
        final List<CronProcessDto.Response> result = cronProcessService
            .findAllCronProcess("0.0.0.0", UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //then
        Assertions.assertThat(result.get(0).getPid()).isEqualTo("1");
        Assertions.assertThat(result.get(0).getStartTime()).isEqualTo(timestamp);
        Assertions.assertThat(result.get(1).getPid()).isEqualTo("2");
        Assertions.assertThat(result.get(1).getStartTime()).isEqualTo(timestamp);


    }

    @Test
    void findAllProcess_해당_잡의_프로세스가_존재하지_않는_경우() {
        // 해당하는 잡의 프로세스가 존재하지 않는 경우
        //given
        MockitoAnnotations.openMocks(this);
        BDDMockito.given(cronProcessRepository.findAllByCronJob_Id(
            UUID.fromString("123e4567-e89b-12d3-a456-556642440000")))
            .willReturn(new ArrayList<>());

        //when
        final List<CronProcessDto.Response> result = cronProcessService
            .findAllCronProcess("0.0.0.0", UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //then
        Assertions.assertThat(result.isEmpty()).isEqualTo(true);
    }

    @Test
    void makeProcess_크론잡이_존재히는_경우() {
        //Cron Job 이 존재하는 경우
        //given
        MockitoAnnotations.openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        BDDMockito.given(cronProcessRepository.save(any(CronProcess.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());
        Optional<CronJob> testCronJob = Optional.of(new CronJob());
        testCronJob.get().setId(UUID.fromString("123e4567-e89b-12d3-a456-556642440001"));
        testCronJob.get().setCronExpr("test");
        testCronJob.get().setCronName("test");
        BDDMockito.given(
            cronJobRepository.findById(UUID.fromString("123e4567-e89b-12d3-a456-556642440001")))
            .willReturn(testCronJob);

        //when
        CronProcessDto.Request request = new Request();
        request.setPid("28");
        request.setStartTime(timestamp);
        Response response = cronProcessService
            .makeCronProcess("0.0.0.0",UUID.fromString("123e4567-e89b-12d3-a456-556642440001"),
                request);

        //then
        Assertions.assertThat(response.getPid()).isEqualTo("28");
        Assertions.assertThat(response.getStartTime()).isEqualTo(timestamp);
    }

    @Test
    void makeProcess_크론잡이_존재하지_않는_경우() {
        //Cron Job 이 존재하지 않는 경우

        MockitoAnnotations.openMocks(this);
        BDDMockito.given(
            cronJobRepository.findById(UUID.fromString("123e4567-e89b-12d3-a456-556642440001")))
            .willReturn(Optional.empty());

        //then
        org.junit.jupiter.api.Assertions.assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            Response response = cronProcessService
                .findCronProcess("0.0.0.0", UUID.fromString("123e4567-e89b-12d3-a456-556642440001"),
                    "28");
        });
    }

    @Test
    void findProcess_크론_프로세스가_존재하는_경우() {
        //프로세스가 존재하는 경우
        //given
        MockitoAnnotations.openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronJob cronJob = new CronJob();
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        Optional<CronProcess> cronProcess = Optional.of(new CronProcess());
        cronProcess.get().setId(1L);
        cronProcess.get().setStartTime(timestamp);
        cronProcess.get().setPid("28");
        cronProcess.get().setCronJob(cronJob);

        BDDMockito.given(
            cronProcessRepository.findByPid("28"))
            .willReturn(cronProcess);

        //when
        Response response = cronProcessService
            .findCronProcess("0.0.0.0", UUID.fromString("123e4567-e89b-12d3-a456-556642440001"),
                "28");

        //then
        Assertions.assertThat(response.getPid()).isEqualTo("28");
        Assertions.assertThat(response.getStartTime()).isEqualTo(timestamp);
    }

    @Test
    void findProcess_크론_프로세스가_존재하지_않는_경우() {
        //given
        MockitoAnnotations.openMocks(this);
        BDDMockito.given(
            cronProcessRepository.findByPid("28"))
            .willReturn(Optional.empty());

        //then
        org.junit.jupiter.api.Assertions.assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            Response response = cronProcessService
                .findCronProcess("0.0.0.0", UUID.fromString("123e4567-e89b-12d3-a456-556642440001"),
                    "28");
        });

    }


    @Test
    void changeCronProcess_크론프로세스가_존재하는_경우() {
        //given
        MockitoAnnotations.openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        BDDMockito.given(cronProcessRepository.save(any(CronProcess.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        CronJob cronJob = new CronJob();
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        Optional<CronProcess> testCronPrcess = Optional.of(new CronProcess());
        testCronPrcess.get().setStartTime(timestamp);
        testCronPrcess.get().setId(1L);
        testCronPrcess.get().setPid("28");
        testCronPrcess.get().setCronJob(cronJob);
        BDDMockito.given(cronProcessRepository.findByPid("28")).willReturn(testCronPrcess);

        //when
        CronProcessDto.Request request = new CronProcessDto.Request();
        request.setEndTime(timestamp);
        Response response = cronProcessService
            .changeCronProcess("0.0.0.0", UUID.fromString("123e4567-e89b-12d3-a456-556642440001"),
                "28", request);
        //then
        Assertions.assertThat(response.getEndTime()).isEqualTo(timestamp);
    }

    @Test
    void changeCronProcess_크론프로세스가_존재하지_않는_경우() {
        //given
        MockitoAnnotations.openMocks(this);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BDDMockito.given(cronProcessRepository.findByPid("28")).willReturn(Optional.empty());

        //then
        org.junit.jupiter.api.Assertions.assertThrows(CronProcessNotFoundException.class, () -> {
            //when
            CronProcessDto.Request request = new CronProcessDto.Request();
            request.setEndTime(timestamp);
            Response process = cronProcessService
                .changeCronProcess("0.0.0.0",
                    UUID.fromString("123e4567-e89b-12d3-a456-556642440001"), "2", request);
        });

    }
}