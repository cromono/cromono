package gabia.cronMonitoring.integration;

import static gabia.cronMonitoring.util.CronMonitorUtil.jsonStrToObj;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gabia.cronMonitoring.controller.CronJobController;
import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.dto.CronJobResult;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.service.CronJobService;
import gabia.cronMonitoring.util.CronMonitorUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active:common")
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
public class CronJobIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CronJobService cronJobService;
    @Autowired
    CronJobController cronJobController;
    @Autowired
    CronJobRepository cronJobRepository;
    @Autowired
    CronServerRepository cronServerRepository;

    @Test
    @Transactional
    public void 서버로_크론_JOB_조회_성공() throws Exception {
        //given
        String serverIp = "192.168.0.1";
        CronServer cronServer = new CronServer(serverIp);
        cronServerRepository.save(cronServer);

        for (int i = 0; i < 10; i++) {
            cronJobRepository.save(
                new CronJob(null, "test" + i + ".sh", "* * * * * test" + i + ".sh", new Date(),
                    new Date(), cronServer));
        }

        //when,
        String returnJson = mockMvc.perform(get("/cron-servers/{serverIp}/cron-jobs", serverIp))
            //then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(10)))
            .andDo(print())
            .andReturn().getResponse().getContentAsString();

        CronJobResult<List<HashMap<String, String>>> result = CronMonitorUtil
            .jsonStrToObj(returnJson, CronJobResult.class);
        // CronJobResult<List<CronJobDTO>> result = CronMonitorUtil.jsonStrToObj(returnJson, CronJobResult.class);
        System.out.println();
        for (int i = 0; i < 10; i++) {
            Assertions.assertThat(result.getData().get(i).get("cronName"))
                .isEqualTo("test" + i + ".sh");
        }
        System.out.println(result.getData().size());

    }

    @Test
    @Transactional
    public void 크론_JOB_생성_성공() throws Exception {
        String serverIp = "192.168.0.1";
        CronServer cronServer = new CronServer(serverIp);
        cronServerRepository.save(cronServer);

        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJobDTO cronJobDTO = new CronJobDTO(null, cronName, cronExpr, sDate, eDate,
            serverIp);

        String requestJson = CronMonitorUtil.objToJson(cronJobDTO);

        //when
        String returnJson = mockMvc.perform(post("/cron-servers/{serverIp}/cron-jobs", serverIp)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            //then
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn().getResponse().getContentAsString();

        CronJobResult<String> result = jsonStrToObj(returnJson, CronJobResult.class);
        Assertions.assertThat(result.getData()).isNotNull();

    }

    @Test
    @Transactional
    public void 크론_JOB_수정_성공() throws Exception {
        //given
        String serverIp = "192.168.0.1";
        CronServer cronServer = new CronServer(serverIp);
        cronServerRepository.save(cronServer);

        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJob cronJob = new CronJob(null, cronName, cronExpr, sDate, eDate,
            cronServer);
        cronJob = cronJobRepository.save(cronJob);

        CronJobDTO cronJobDTO = new CronJobDTO(cronJob.getId(), "수정된이름", "수정된표현", new Date(),
            new Date(), serverIp);
        String requestJson = CronMonitorUtil.objToJson(cronJobDTO);

        //when
        mockMvc
            .perform(
                patch("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJob.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
            //then
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        CronJob updatedCronJob = cronJobRepository.findById(cronJob.getId()).get();
        Assertions.assertThat(updatedCronJob).isNotNull();
        Assertions.assertThat(updatedCronJob.getCronName()).isEqualTo("수정된이름");
        Assertions.assertThat(updatedCronJob.getCronExpr()).isEqualTo("수정된표현");


    }

    @Test
    @Transactional
    public void 크론_JOB_삭제_성공() throws Exception {
        //given
        String serverIp = "192.168.0.1";
        CronServer cronServer = new CronServer(serverIp);
        cronServerRepository.save(cronServer);

        int firstSize = cronJobRepository.findByServer(serverIp).size();

        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJob cronJob = new CronJob(null, cronName, cronExpr, sDate, eDate,
            cronServer);
        cronJob = cronJobRepository.save(cronJob);

        int addedSize = cronJobRepository.findByServer(serverIp).size();

        //when
        mockMvc
            .perform(
                delete("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJob.getId())
                    .contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isAccepted())
            .andDo(print())
            .andReturn();

        int deletedSize = cronJobRepository.findByServer(serverIp).size();

        Assertions.assertThat(firstSize + 1).isEqualTo(addedSize);
        Assertions.assertThat(deletedSize).isEqualTo(firstSize);
    }

}