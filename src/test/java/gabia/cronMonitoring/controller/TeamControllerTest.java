package gabia.cronMonitoring.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gabia.cronMonitoring.dto.TeamDTO;
import gabia.cronMonitoring.service.TeamService;
import gabia.cronMonitoring.util.CronMonitorUtil;
import java.util.LinkedList;
import java.util.List;
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
@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    TeamService teamService;

    @InjectMocks
    TeamController teamController;

    @Before
    public void setUpMockMVC() {
        teamController = new TeamController(teamService);
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
        CronMonitorUtil.initForTest();
    }

    @Test
    public void 모든_팀_조회() throws Exception {
        //given
        List<TeamDTO.Response> teamResponseList = new LinkedList<>();
        teamResponseList.add(new TeamDTO.Response("team1", "크론모니터링"));
        teamResponseList.add(new TeamDTO.Response("team2", "웹훅"));
        given(teamService.findTeamAll()).willReturn(teamResponseList);
        //when
        mockMvc.perform(get("/teams"))
            //then
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].teamAccount").value("team1"))
            .andExpect(jsonPath("$.[0].name").value("크론모니터링"))
            .andExpect(jsonPath("$.[1].teamAccount").value("team2"))
            .andExpect(jsonPath("$.[1].name").value("웹훅"))
            .andReturn();
    }

    @Test
    public void 단일_팀_조회_성공() throws Exception {
        //given
        TeamDTO.Response response = new TeamDTO.Response("team1", "크론모니터링");
        given(teamService.findTeam("team1")).willReturn(response);

        //when
        mockMvc.perform(get("/teams/{teamId}", "team1")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("크론모니터링"))
            .andExpect(jsonPath("$.teamAccount").value("team1"))
            .andReturn();

    }


    @Test
    public void 팀_생성_성공() throws Exception {
        //given
        TeamDTO.Request request = new TeamDTO.Request("team1", "크론모니터링", "yhw");
        TeamDTO.Response response = new TeamDTO.Response("team1", "크론모니터링");
        given(teamService.createTeam(request, request.getUserAccount())).willReturn(response);
        String requestJson = CronMonitorUtil.objToJson(request);

        //when
        mockMvc.perform(post("/teams")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            //then
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.teamAccount").value("team1"))
            .andReturn();
    }

    @Test
    public void 팀명_수정_성공() throws Exception {
        //given
        TeamDTO.Request request = new TeamDTO.Request("team1", "크론모니터링", "yhw");
        TeamDTO.Response response = new TeamDTO.Response("team1", "크론모니터링");
        given(teamService.changeTeam(request, request.getUserAccount())).willReturn(response);
        String requestJson = CronMonitorUtil.objToJson(request);

        //when
        mockMvc.perform(patch("/teams/{teamId}", "team1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            //then
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.teamAccount").value("team1"))
            .andReturn();
    }

    @Test
    public void 팀명_수정_실패_pathparam없음() throws Exception {
        String teamAccount = null;
        mockMvc.perform(patch("/teams/{teamId}", "team1"))
            //then
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andReturn();
    }

    @Test
    public void 팀_삭제_성공() throws Exception {
        //given
        TeamDTO.Request request = new TeamDTO.Request("team1", "크론모니터링", "yhw");
        String requestJson = CronMonitorUtil.objToJson(request);
        //when
        mockMvc.perform(delete("/teams/{teamId}", "team1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            //then
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void 팀_삭제_실패_pathparam없음() throws Exception {
        String teamAccount = null;
        mockMvc.perform(delete("/teams/{teamId}", "team1"))
            //then
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andReturn();
    }
}
