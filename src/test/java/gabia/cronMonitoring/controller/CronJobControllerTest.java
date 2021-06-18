package gabia.cronMonitoring.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gabia.cronMonitoring.Util.CronMonitorUtil;
import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.dto.CronJobResult;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.service.CronJobService;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(CronJobController.class)
public class CronJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    CronJobService cronJobService;

    @InjectMocks
    CronJobController cronJobController;

    @Before
    public void setup() {
        cronJobController = new CronJobController(cronJobService);
        mockMvc = MockMvcBuilders.standaloneSetup(cronJobController).build();
    }

    @Test
    public void 서버_이름으로_크론_JOB_조회_성공() throws Exception {
        //given
        String serverIp = "192.168.0.1";
        List<CronJobDTO> cronJobDTOs = new LinkedList<>();
        cronJobDTOs.add(
            new CronJobDTO(UUID.randomUUID(), "test1.sh", "* * * * * test1.sh", new Date(),
                new Date(), serverIp));
        cronJobDTOs.add(
            new CronJobDTO(UUID.randomUUID(), "test2.sh", "* * * * * test2.sh", new Date(),
                new Date(), serverIp));

        given(cronJobService.readCronJobListByServer(serverIp)).willReturn(cronJobDTOs);

        //when,
        mockMvc.perform(get("/cron-servers/{serverIp}/cron-jobs", serverIp))
            //then
            .andExpect(status().isOk());


    }

    @Test
    public void 서버_이름으로_크론_JOB_조회_실패() throws Exception {
        //given
        String serverIp = null;
        //when,
        mockMvc.perform(get("/cron-servers/{serverIp}/cron-jobs", serverIp))
            //then
            .andExpect(status().is4xxClientError())
            .andDo(print())
            .andReturn();
    }

    @Test
    public void 크론_JOB_생성_성공() throws Exception {
        String serverIp = "192.168.0.1";
        UUID cronJobId = UUID.randomUUID();
        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJobDTO cronJobDTO = new CronJobDTO(cronJobId, cronName, cronExpr, sDate, eDate,
            serverIp);
        CronJob cronJob = new CronJob(cronJobId, cronName, cronExpr, sDate, eDate,
            new CronServer());

        String requestJson = CronMonitorUtil.objToJson(cronJobDTO);
        String responseJson = CronMonitorUtil.objToJson(new CronJobResult<>(cronJobId));

        given(cronJobService.createCronJob(cronJobDTO)).willReturn(cronJobDTO);

        //when,
        mockMvc.perform(post("/cron-servers/{serverIp}/cron-jobs", serverIp)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            //then
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson))
            .andDo(print())
            .andReturn();
    }

    @Test
    public void 크론_JOB_생성_실패() throws Exception {

        String serverIp = null;
        //when,
        mockMvc.perform(post("/cron-servers/{serverIp}/cron-jobs", serverIp))
            //then
            .andExpect(status().is4xxClientError())
            .andDo(print())
            .andReturn();
    }

    @Test
    public void 크론_JOB_수정_성공() throws Exception {
        //given
        UUID cronJobId = UUID.randomUUID();
        String serverIp = "192.168.0.1";
        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJobDTO cronJobDTO = new CronJobDTO(cronJobId, cronName, cronExpr, sDate, eDate,
            serverIp);

        String requestJson = CronMonitorUtil.objToJson(cronJobDTO);
        String responseJson = CronMonitorUtil.objToJson(new CronJobResult<>(cronJobId));

        given(cronJobService
            .updateCronJob(cronJobId, serverIp, cronName, cronExpr, sDate, eDate))
            .willReturn(cronJobDTO);
        //when
        mockMvc
            .perform(patch("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            //then
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson))
            .andDo(print())
            .andReturn();

    }


    @Test
    public void 크론_JOB_수정_실패() throws Exception {
        UUID cronJobId = null;
        String serverIp = null;

        mockMvc
            .perform(patch("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJobId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andDo(print())
            .andReturn();

    }


    @Test
    public void 크론_JOB_삭제_성공() throws Exception {
        //given
        UUID cronJobId = UUID.randomUUID();
        String serverIp = "192.168.0.1";
        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJobDTO cronJobDTO = new CronJobDTO(cronJobId, cronName, cronExpr, sDate, eDate,
            serverIp);

        String requestJson = CronMonitorUtil.objToJson(cronJobDTO);

        given(cronJobService
            .deleteCronJob(cronJobId))
            .willReturn(true);
        //when
        mockMvc
            .perform(delete("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            //then
            .andExpect(status().isAccepted())
            .andDo(print())
            .andReturn();
    }

    @Test
    public void 크론_JOB_삭제_실패() throws Exception {

        UUID cronJobId = null;
        String serverIp = null;

        mockMvc
            .perform(delete("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJobId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andDo(print())
            .andReturn();
    }


}