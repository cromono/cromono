package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import gabia.cronMonitoring.dto.TeamCronJobDTO;
import gabia.cronMonitoring.dto.TeamCronJobDTO.Request;
import gabia.cronMonitoring.dto.TeamCronJobDTO.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamCronJob;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.team.TeamNotFoundException;
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.TeamCronJobRepository;
import gabia.cronMonitoring.repository.TeamRepository;
import java.util.LinkedList;
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
class TeamCronJobServiceTest {

    @Mock
    private TeamCronJobRepository teamCronJobRepository;

    @Mock
    private CronJobRepositoryDataJpa cronJobRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamCronJobService teamCronJobService;

    @Test
    public void findAllTeamCronJob() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        Team team = new Team();
        team.setAccount("test");
        team.setId(1L);
        team.setName("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        CronJob cronJob2 = new CronJob();
        cronJob2.setId(UUID.randomUUID());
        cronJob2.setServer(cronServer);
        cronJob2.setCronExpr("test2");
        cronJob2.setCronName("test2");

        TeamCronJob teamCronJob1 = TeamCronJob.builder()
            .id(1L)
            .team(team)
            .cronJob(cronJob)
            .build();

        TeamCronJob teamCronJob2 = TeamCronJob.builder()
            .id(2L)
            .team(team)
            .cronJob(cronJob2)
            .build();

        List<TeamCronJob> teamCronJobList = new LinkedList<>();

        teamCronJobList.add(teamCronJob1);
        teamCronJobList.add(teamCronJob2);

        given(teamCronJobRepository.findByTeamAccount(any()))
            .willReturn(teamCronJobList);

        //when
        List<TeamCronJobDTO.Response> allTeamReadAuth = teamCronJobService
            .findAllTeamCronJob("test");

        //then
        Assertions.assertThat(allTeamReadAuth.get(0).getTeamAccount()).isEqualTo("test");
        Assertions.assertThat(allTeamReadAuth.get(0).getCronJobId()).isEqualTo(cronJob.getId());
        Assertions.assertThat(allTeamReadAuth.get(1).getTeamAccount()).isEqualTo("test");
        Assertions.assertThat(allTeamReadAuth.get(1).getCronJobId()).isEqualTo(cronJob2.getId());
    }

    @Test
    void addTeamCronJob_팀_크론잡_모두_존재하는_경우() throws JsonProcessingException {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        Team team = new Team();
        team.setId(1L);
        team.setAccount("test");
        team.setName("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        TeamCronJob teamCronJob1 = TeamCronJob.builder()
            .id(1L)
            .team(team)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(teamRepository.findByAccount("test")).willReturn(Optional.of(team));

        given(teamCronJobRepository.save(any(TeamCronJob.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        TeamCronJobDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());

        Response response = teamCronJobService.addTeamCronJob("test", request);

        //then
        Assertions.assertThat(response.getTeamAccount()).isEqualTo("test");
        Assertions.assertThat(response.getCronJobId()).isEqualTo(cronJob.getId());

    }

    @Test
    void addTeamCronJob_팀이_없는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        Team team = new Team();
        team.setId(1L);
        team.setAccount("test");
        team.setName("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        TeamCronJob teamCronJob1 = TeamCronJob.builder()
            .id(1L)
            .team(team)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(teamRepository.findByAccount("test")).willReturn(Optional.empty());

        given(teamCronJobRepository.save(any(TeamCronJob.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        assertThrows(TeamNotFoundException.class, () -> {
            TeamCronJobDTO.Request request = new Request();
            request.setCronJobId(cronJob.getId());
            Response response = teamCronJobService.addTeamCronJob("test", request);
        });

    }

    @Test
    void addTeamCronJob_크론잡이_없는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        Team team = new Team();
        team.setId(1L);
        team.setAccount("test");
        team.setName("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        TeamCronJob teamCronJob1 = TeamCronJob.builder()
            .id(1L)
            .team(team)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
        given(teamRepository.findByAccount("test")).willReturn(Optional.of(team));

        given(teamCronJobRepository.save(any(TeamCronJob.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        assertThrows(CronJobNotFoundException.class, () -> {
            TeamCronJobDTO.Request request = new Request();
            request.setCronJobId(cronJob.getId());
            Response response = teamCronJobService.addTeamCronJob("test", request);
        });

    }

    @Test
    void removeTeamCronJob_크론잡_팀이_존재하는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        Team team = new Team();
        team.setId(1L);
        team.setAccount("test");
        team.setName("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        TeamCronJob teamCronJob1 = TeamCronJob.builder()
            .id(1L)
            .team(team)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(teamRepository.findByAccount("test")).willReturn(Optional.of(team));

        teamCronJobService.removeTeamCronJob(team.getAccount(), cronJob.getId());

        assertThrows(CronJobNotFoundException.class, () -> {
            given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
            given(teamRepository.findByAccount("test")).willReturn(Optional.empty());

            teamCronJobService.removeTeamCronJob(team.getAccount(), cronJob.getId());
        });

    }

    @Test
    void removeTeamCronJob_팀이_존재하지_않는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        List<TeamCronJob> teamCronJobList = new LinkedList<>();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(teamRepository.findByAccount("test")).willReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> {
            teamCronJobService.removeTeamCronJob("test", cronJob.getId());
        });

    }

    @Test
    void removeTeamCronJob_크론잡이_존재하지_않는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        Team team = new Team();
        team.setId(1L);
        team.setAccount("test");
        team.setName("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
        given(teamRepository.findByAccount("test")).willReturn(Optional.of(team));

        assertThrows(CronJobNotFoundException.class, () -> {
            teamCronJobService.removeTeamCronJob(team.getAccount(), cronJob.getId());
        });
    }


}