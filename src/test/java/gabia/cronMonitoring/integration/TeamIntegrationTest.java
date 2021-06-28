package gabia.cronMonitoring.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gabia.cronMonitoring.controller.TeamController;
import gabia.cronMonitoring.dto.TeamDTO;
import gabia.cronMonitoring.entity.Enum.AuthType;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamUser;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.repository.TeamRepository;
import gabia.cronMonitoring.repository.TeamUserRepository;
import gabia.cronMonitoring.repository.UserRepository;
import gabia.cronMonitoring.service.TeamService;
import gabia.cronMonitoring.util.CronMonitorUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TeamIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TeamController teamController;

    @Autowired
    TeamService teamService;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamUserRepository teamUserRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    public void 팀_목록_조회_성공() throws Exception {
        User user = User.builder().name("윤현우").email("yhw@gabia.com").password("1234")
            .account("yhw").build();
        userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Team team = Team.builder().name("teamName" + i).account("account" + i).build();
            teamRepository.save(team);
            teamUserRepository.save(
                TeamUser.builder().team(team).user(user).authority(AuthType.UserManager).build());
        }

        //jsonPath를 이용한 검증
        String returnJson = mockMvc.perform(get("/teams"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
            .andExpect(jsonPath("$.[0].teamAccount").value("account0"))
            .andDo(print())
            .andReturn().getResponse().getContentAsString();

        //객체화해 검증 추구 구현
        //List<TeamDTO> result = CronMonitorUtil.jsonStrToObj(returnJson, List.class);

    }

    @Test
    @Transactional
    public void 팀_조회_성공() throws Exception {
        User user = User.builder().name("윤현우").email("yhw@gabia.com").password("1234")
            .account("yhw").build();
        userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Team team = Team.builder().name("teamName" + i).account("account" + i).build();
            teamRepository.save(team);
            teamUserRepository.save(
                TeamUser.builder().team(team).user(user).authority(AuthType.UserManager).build());
        }

        //jsonPath를 이용한 검증
        String returnJson = mockMvc.perform(get("/teams/{teamId}", "account2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.teamAccount").value("account2"))
            .andDo(print())
            .andReturn().getResponse().getContentAsString();

        //객체화해 검증 추구 구현
        //List<TeamDTO> result = CronMonitorUtil.jsonStrToObj(returnJson, List.class);

    }

    @Test
    @Transactional
    public void 팀_생성_성공() throws Exception {
        User user = User.builder().name("윤현우").email("yhw@gabia.com").password("1234")
            .account("yhw").build();
        userRepository.save(user);

        TeamDTO.Request teamDTORequest = new TeamDTO.Request();
        teamDTORequest.setTeamAccount("team1");
        teamDTORequest.setName("cronmonitoring");
        teamDTORequest.setUserAccount("yhw");
        String requestJson = CronMonitorUtil.objToJson(teamDTORequest);

        //jsonPath를 이용한 검증
        String returnJson = mockMvc.perform(post("/teams/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn().getResponse().getContentAsString();
    }

    @Test
    @Transactional
    public void 팀_수정_성공() throws Exception {
        User user = User.builder().name("윤현우").email("yhw@gabia.com").password("1234")
            .account("yhw").build();
        userRepository.save(user);

        Team team = Team.builder().account("team1").name("cronmonitor").build();
        teamRepository.save(team);

        TeamUser teamUser =
            TeamUser.builder().user(user).team(team).authority(AuthType.UserManager).build();
        teamUserRepository.save(teamUser);

        TeamDTO.Request teamDTORequest = new TeamDTO.Request();
        teamDTORequest.setTeamAccount("team1");
        teamDTORequest.setName("webhook");
        teamDTORequest.setUserAccount("yhw");
        String requestJson = CronMonitorUtil.objToJson(teamDTORequest);

        //jsonPath를 이용한 검증
        String returnJson = mockMvc.perform(patch("/teams/{teamId}", team.getAccount())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("webhook"))
            .andDo(print())
            .andReturn().getResponse().getContentAsString();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void 팀_삭제_성공() throws Exception {
        User user = User.builder().name("윤현우").email("yhw@gabia.com").password("1234")
            .account("yhw").build();
        userRepository.save(user);

        Team team = Team.builder().account("team1").name("cronmonitor").build();
        teamRepository.save(team);

        TeamUser teamUser =
            TeamUser.builder().user(user).team(team).authority(AuthType.UserManager).build();
        teamUserRepository.save(teamUser);

        TeamDTO.Request teamDTORequest = new TeamDTO.Request();
        teamDTORequest.setTeamAccount("team1");
        teamDTORequest.setName("webhook");
        teamDTORequest.setUserAccount("yhw");

        String requestJson = CronMonitorUtil.objToJson(teamDTORequest);

        //jsonPath를 이용한 검증
        String returnJson = mockMvc.perform(delete("/teams/{teamId}", team.getAccount())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("webhook"))
            .andDo(print())
            .andReturn().getResponse().getContentAsString();
    }

}
