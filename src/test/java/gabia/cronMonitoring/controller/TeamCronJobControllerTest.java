package gabia.cronMonitoring.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.TeamCronJobDTO;
import gabia.cronMonitoring.dto.TeamCronJobDTO.Request;
import gabia.cronMonitoring.exception.cron.handler.ControllerExceptionHandler;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.team.TeamNotFoundException;
import gabia.cronMonitoring.service.TeamCronJobService;
import java.util.ArrayList;
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

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(TeamCronJobController.class)
public class TeamCronJobControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    TeamCronJobService teamCronJobService;

    @InjectMocks
    ControllerExceptionHandler controllerExceptionHandler;

    @InjectMocks
    TeamCronJobController teamCronJobController;

    @Before
    public void setUpMockMvc() {
        teamCronJobController = new TeamCronJobController(teamCronJobService);
        mvc = standaloneSetup(teamCronJobController)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
    }

    @Test
    public void 모든_팀_크론잡_조회() throws Exception {

        //given
        List<TeamCronJobDTO.Response> allResponse = new ArrayList<>();

        TeamCronJobDTO.Response testResponse = new TeamCronJobDTO.Response();
        testResponse.setTeamAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        TeamCronJobDTO.Response testResponse2 = new TeamCronJobDTO.Response();
        testResponse2.setTeamAccount("test");
        testResponse2.setCronJobId(UUID.randomUUID());

        allResponse.add(testResponse);
        allResponse.add(testResponse2);

        //when
        given(teamCronJobService.findAllTeamCronJob("test")).willReturn(allResponse);

        //then
        mvc.perform(get("/cron-read-auths/teams/{teamId}/crons/", "test"))
            .andDo(print())
            .andExpect(jsonPath("$[0].teamAccount", "test").exists())
            .andExpect(jsonPath("$[1].teamAccount", "test").exists())
            .andExpect(jsonPath("$[0].cronJobId", testResponse.getCronJobId()).exists())
            .andExpect(jsonPath("$[1].cronJobId", testResponse2.getCronJobId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 팀_크론잡_추가() throws Exception {

        //given
        TeamCronJobDTO.Response testResponse = new TeamCronJobDTO.Response();
        testResponse.setTeamAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when
        TeamCronJobDTO.Request request = new Request();
        request.setCronJobId(UUID.randomUUID());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(teamCronJobService.addTeamCronJob("test", request))
            .willReturn(testResponse);

        //then
        mvc.perform(post("/cron-read-auths/teams/{teamId}/crons/", "test")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.teamAccount", "test").exists())
            .andExpect(jsonPath("$.cronJobId", testResponse.getCronJobId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 팀_크론잡_추가_크론잡이_없는_경우() throws Exception {

        //given
        TeamCronJobDTO.Response testResponse = new TeamCronJobDTO.Response();
        testResponse.setTeamAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when
        TeamCronJobDTO.Request request = new Request();
        request.setCronJobId(UUID.randomUUID());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(teamCronJobService.addTeamCronJob("test", request))
            .willThrow(new CronJobNotFoundException());

        //then
        mvc.perform(post("/cron-read-auths/teams/{teamId}/crons/", "test")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.errorMsg", "Do not find Cron Job").exists())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 팀_크론잡_추가_팀이_없는_경우() throws Exception {

        //given
        TeamCronJobDTO.Response testResponse = new TeamCronJobDTO.Response();
        testResponse.setTeamAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when
        TeamCronJobDTO.Request request = new Request();
        request.setCronJobId(UUID.randomUUID());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(teamCronJobService.addTeamCronJob("test", request))
            .willThrow(new TeamNotFoundException());

        //then
        mvc.perform(post("/cron-read-auths/teams/{teamId}/crons/", "test")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.errorMsg", "Do not find Team").exists())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 팀_크론잡_삭제() throws Exception {

        //given
        TeamCronJobDTO.Response testResponse = new TeamCronJobDTO.Response();
        testResponse.setTeamAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when

        //then
        mvc.perform(delete("/cron-read-auths/teams/{teamId}/crons/{cronJobId}", "test",
            testResponse.getCronJobId()))
            .andDo(print())
            .andExpect(status().isOk());
    }
}