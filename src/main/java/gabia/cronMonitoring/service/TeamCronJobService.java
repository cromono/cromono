package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.TeamCronJobDTO;
import gabia.cronMonitoring.dto.TeamCronJobDTO.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamCronJob;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.team.TeamNotFoundException;
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.TeamCronJobRepository;
import gabia.cronMonitoring.repository.TeamRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamCronJobService {

    private final CronJobRepositoryDataJpa cronJobRepository;
    private final TeamCronJobRepository teamCronJobRepository;
    private final TeamRepository teamRepository;

    public List<Response> findAllTeamCronJob(String account) {

        List<TeamCronJobDTO.Response> responses = teamCronJobRepository.findByTeamAccount(account)
            .stream()
            .map(dto -> TeamCronJobDTO.Response.from(dto))
            .collect(Collectors.toList());

        return responses;

    }

    @Transactional
    public TeamCronJobDTO.Response addTeamCronJob(String account, TeamCronJobDTO.Request request) {

        CronJob cronJob = cronJobRepository.findById(request.getCronJobId())
            .orElseThrow(() -> new CronJobNotFoundException());

        Team team = teamRepository.findByAccount(account)
            .orElseThrow(() -> new TeamNotFoundException());

        TeamCronJob teamCronJob = TeamCronJob.builder()
            .team(team)
            .cronJob(cronJob)
            .build();

        TeamCronJob savedTeamCronJob = teamCronJobRepository.save(teamCronJob);

        Response response = Response.from(savedTeamCronJob);

        return response;
    }

    @Transactional
    public void removeTeamCronJob(String account, UUID cronJobId) {
        CronJob cronJob = cronJobRepository.findById(cronJobId)
            .orElseThrow(() -> new CronJobNotFoundException());

        Team team = teamRepository.findByAccount(account)
            .orElseThrow(() -> new TeamNotFoundException());

        teamCronJobRepository.deleteByCronJobIdAndTeamAccount(cronJobId, account);
    }


}
