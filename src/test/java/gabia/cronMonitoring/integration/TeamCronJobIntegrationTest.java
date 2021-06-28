package gabia.cronMonitoring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.controller.TeamCronJobController;
import gabia.cronMonitoring.dto.TeamCronJobDTO;
import gabia.cronMonitoring.dto.TeamCronJobDTO.Request;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamCronJob;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.repository.TeamCronJobRepository;
import gabia.cronMonitoring.repository.TeamRepository;
import gabia.cronMonitoring.service.CronProcessService;
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
@SpringBootTest
@Transactional
public class TeamCronJobIntegrationTest {

    private MockMvc mvc;

    @Autowired
    CronProcessService cronProcessService;

    @Autowired
    CronServerRepository cronServerRepository;

    @Autowired
    CronJobRepository cronJobRepository;

    @Autowired
    CronProcessRepository cronProcessRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamCronJobRepository teamCronJobRepository;

    @Autowired
    TeamCronJobController teamCronJobController;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUpMockMvc() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void 모든_팀크론잡_조회() throws Exception {

        //given
        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        CronJob cronJob2 = new CronJob();
        cronJob2.setServer(cronServer);
        cronJob2.setCronExpr("test2");
        cronJob2.setCronName("test2");

        cronJobRepository.save(cronJob);
        cronJobRepository.save(cronJob2);

        Team team = new Team();
        team.setAccount("Lucas");
        team.setName("jyj");

        teamRepository.save(team);

        TeamCronJob teamCronJob = TeamCronJob.builder()
            .team(team)
            .cronJob(cronJob)
            .build();

        TeamCronJob teamCronJob2 = TeamCronJob.builder()
            .team(team)
            .cronJob(cronJob2)
            .build();

        TeamCronJob savedTeamCronJob = teamCronJobRepository.save(teamCronJob);
        TeamCronJob savedTeamCronJob2 = teamCronJobRepository.save(teamCronJob2);
        //when

        //then
        mvc.perform(
            get("/cron-read-auths/teams/{teamId}/crons/", "Lucas"))
            .andDo(print())
            .andExpect(jsonPath("$[0].teamAccount", savedTeamCronJob.getTeam().getId()).exists())
            .andExpect(jsonPath("$[1].teamAccount", savedTeamCronJob2.getTeam().getId()).exists())
            .andExpect(jsonPath("$[0].cronJobId", savedTeamCronJob.getCronJob().getId()).exists())
            .andExpect(jsonPath("$[1].cronJobId", savedTeamCronJob2.getCronJob().getId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 팀크론잡_추가() throws Exception {

        //given
        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        Team team = new Team();
        team.setAccount("Lucas");
        team.setName("jyj");
        teamRepository.save(team);

        TeamCronJob teamCronJob = TeamCronJob.builder()
            .team(team)
            .cronJob(cronJob)
            .build();

        //when
        TeamCronJobDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(
            post("/cron-read-auths/teams/{teamId}/crons/", "Lucas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andDo(print())
            .andExpect(jsonPath("$.teamAccount", team.getId()).exists())
            .andExpect(jsonPath("$.cronJobId", cronJob.getId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 팀크론잡_삭제() throws Exception {

        //given
        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        Team team = new Team();
        team.setAccount("Lucas");
        team.setName("jyj");
        teamRepository.save(team);

        TeamCronJob teamCronJob = TeamCronJob.builder()
            .team(team)
            .cronJob(cronJob)
            .build();
        teamCronJobRepository.save(teamCronJob);

        //when
        TeamCronJobDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(
            post("/cron-read-auths/teams/{teamId}/crons/", "Lucas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andDo(print())
            .andExpect(jsonPath("$.teamAccount", team.getId()).exists())
            .andExpect(jsonPath("$.cronJobId", cronJob.getId()).exists())
            .andExpect(status().isOk());
    }

}
