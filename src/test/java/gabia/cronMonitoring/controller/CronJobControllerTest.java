package gabia.cronMonitoring.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.dto.CronJobResult;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.mapper.CronMapper;
import gabia.cronMonitoring.service.CronJobService;
import java.lang.module.ResolutionException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
    public void findCronJobByServer() throws Exception {
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
    public void createCronJob() throws Exception {
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

        String requestJson = objToJson(cronJobDTO);
        String responseJson = objToJson(new CronJobResult<>(cronJobId));

        given(cronJobService.createCronJob(cronJobDTO)).willReturn(cronJobDTO);

        //when,
        mockMvc.perform(post("/cron-servers/{serverIp}/cron-jobs", serverIp)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson))
            .andDo(print())
            .andReturn();

    }

    @Test
    public void updateCronJob() throws Exception {
        UUID cronJobId = UUID.randomUUID();

        String serverIp = "192.168.0.1";
        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJobDTO cronJobDTO = new CronJobDTO(cronJobId, cronName, cronExpr, sDate, eDate,
            serverIp);

        String requestJson = objToJson(cronJobDTO);
        String responseJson = objToJson(new CronJobResult<>(cronJobId));

        given(cronJobService
            .updateCronJob(cronJobId, serverIp, cronName, cronExpr, sDate, eDate))
            .willReturn(cronJobDTO);

        mockMvc
            .perform(patch("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson))
            .andDo(print())
            .andReturn();

    }

    @Test
    public void testDeleteCronJob() throws Exception {

        UUID cronJobId = UUID.randomUUID();

        String serverIp = "192.168.0.1";
        String cronName = "test1.sh";
        String cronExpr = "* * * * * test1.sh";
        Date sDate = new Date();
        Date eDate = new Date();
        CronJobDTO cronJobDTO = new CronJobDTO(cronJobId, cronName, cronExpr, sDate, eDate,
            serverIp);

        String requestJson = objToJson(cronJobDTO);

        given(cronJobService
            .deleteCronJob(cronJobId))
            .willReturn(true);

        mockMvc
            .perform(delete("/cron-servers/{serverIp}/cron-jobs/{cronJobId}", serverIp, cronJobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isAccepted())
            .andDo(print())
            .andReturn();
    }

    public <T> String objToJson(T obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(obj);
    }
}