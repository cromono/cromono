package gabia.cronMonitoring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.controller.CronProcessController;
import gabia.cronMonitoring.dto.CronProcessDto.Request;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.service.CronProcessService;
import java.sql.Timestamp;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active:common")
@Transactional
public class CronProcessIntegrationTest {

    private MockMvc mvc;

    @Autowired
    CronProcessService cronProcessService;

    @Autowired
    CronProcessController cronProcessController;

    @Autowired
    CronServerRepository cronServerRepository;

    @Autowired
    CronJobRepository cronJobRepository;

    @Autowired
    CronProcessRepository cronProcessRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUpMockMvc() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void 모든_크론_프로세스_조회() throws Exception {

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

        //then
        mvc.perform(
            get("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/", "0.0.0.0",
                savedCronProcess1.getCronJob().getId()))
            .andDo(print())
            .andExpect(jsonPath("$[0].pid", savedCronProcess1.getPid()).exists())
            .andExpect(jsonPath("$[1].pid", savedCronProcess2.getPid()).exists())
            .andExpect(jsonPath("$[0].cronJobId", savedCronProcess1.getCronJob().getId()).exists())
            .andExpect(jsonPath("$[1].cronJobId", savedCronProcess2.getCronJob().getId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 크론_프로세스_생성() throws Exception {
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
        request.setPid("1");
        request.setStartTime(timestamp);
        request.setEndTime(timestamp);

        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(post("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/", "0.0.0.0",
            cronJob1.getId())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(requestJson)
        ).andDo(print())
            .andExpect(jsonPath("$.pid", "1").exists())
            .andExpect(jsonPath("$.cronJobId", cronJob1.getId()).exists())
            .andExpect(status().isOk());

    }

    @Test
    public void 크론_프로세스_조회() throws Exception {
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
            .cronJob(cronJob)
            .build();

        CronProcess savedCronProcess1 = cronProcessRepository.save(cronProcess);

        //when

        //then
        mvc.perform(
            get("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/{pid}", "0.0.0.0",
                savedCronProcess1.getCronJob().getId(), "1"))
            .andDo(print())
            .andExpect(jsonPath("$.pid", savedCronProcess1.getPid()).exists())
            .andExpect(jsonPath("$.cronJobId", savedCronProcess1.getCronJob().getId()).exists())
            .andExpect(status().isOk());

    }

    @Test
    public void updateCronProcess() throws Exception {
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
            .cronJob(cronJob)
            .build();

        CronProcess savedCronProcess1 = cronProcessRepository.save(cronProcess);

        //when

        Request request = new Request();
        request.setPid("1");
        request.setStartTime(timestamp);
        request.setEndTime(timestamp);

        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(
            patch("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/{pid}", "0.0.0.0",
                savedCronProcess1.getCronJob().getId(), "1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson))
            .andDo(print())
            .andExpect(jsonPath("$.pid", savedCronProcess1.getPid()).exists())
            .andExpect(jsonPath("$.cronJobId", savedCronProcess1.getCronJob().getId()).exists())
            .andExpect(jsonPath("$.endTime", savedCronProcess1.getEndTime()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void findCronLogs() throws Exception{
        //given

        //when

        //then
        mvc.perform(
            get("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/{pid}/logs", "0.0.0.0",
                UUID.fromString("123e4567-e89b-12d3-a456-556642440000"), "1"))
            .andDo(print())
            .andExpect(jsonPath("$[0].cronProcess").value("1"))
            .andExpect(jsonPath("$[0].value").value("test"))
            .andExpect(jsonPath("$[1].cronProcess").value("1"))
            .andExpect(jsonPath("$[1].value").value("test"))
            .andExpect(status().isOk());
    }
}