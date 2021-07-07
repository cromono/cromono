package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.TeamCronJobDTO;
import gabia.cronMonitoring.dto.TeamCronJobDTO.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.Team;
import gabia.cronMonitoring.entity.TeamCronJob;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.team.TeamNotFoundException;
import gabia.cronMonitoring.exception.teamcronjob.AlreadyExistTeamCronJobException;
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

    /**
     * Team Cron Job 조회
     *
     * @param account 해당 팀의 account
     * @return List<TeamCronJobDTO.Response></TeamCronJobDTO.Response>
     */
    public List<Response> findAllTeamCronJob(String account) {

        // 해당 하는 Team의 Team Cron Job List 조회
        List<TeamCronJobDTO.Response> responses = teamCronJobRepository.findByTeamAccount(account)
            .stream()
            .map(dto -> TeamCronJobDTO.Response.from(dto))
            .collect(Collectors.toList());

        return responses;

    }

    /**
     * Team Cron Job 추가
     *
     * @param account 해당 팀의 account
     * @param request TeamCronJobDTO.Request
     * @return TeamCronJobDTO.Response
     * @throws CronJobNotFoundException
     * @throws TeamNotFoundException
     * @throws AlreadyExistTeamCronJobException
     */
    @Transactional
    public TeamCronJobDTO.Response addTeamCronJob(String account, TeamCronJobDTO.Request request) {

        // Cron Job과 Team 존재 유무 확인
        CronJob cronJob = cronJobRepository.findById(request.getCronJobId())
            .orElseThrow(() -> new CronJobNotFoundException());

        Team team = teamRepository.findByAccount(account)
            .orElseThrow(() -> new TeamNotFoundException());

        // 이미 존재하는 Team Cron Job인 경우 Exception 발생
        teamCronJobRepository.findByTeamAccountAndCronJobId(account, request.getCronJobId())
            .ifPresent(present -> {
                throw new AlreadyExistTeamCronJobException();
            });

        // Team Cron Job 생성
        TeamCronJob teamCronJob = TeamCronJob.builder()
            .team(team)
            .cronJob(cronJob)
            .build();
        TeamCronJob savedTeamCronJob = teamCronJobRepository.save(teamCronJob);

        Response response = Response.from(savedTeamCronJob);

        return response;
    }

    /**
     * Team Cron Job 삭제
     *
     * @param account   해당 팀의 account
     * @param cronJobId 삭제할 Team Cron Job의 Id
     * @throws CronJobNotFoundException
     * @throws TeamNotFoundException
     */
    @Transactional
    public void removeTeamCronJob(String account, UUID cronJobId) {

        // Cron Job, Team 존재 유무 확인
        cronJobRepository.findById(cronJobId).orElseThrow(() -> new CronJobNotFoundException());
        teamRepository.findByAccount(account).orElseThrow(() -> new TeamNotFoundException());

        // Team Cron Job 삭제
        teamCronJobRepository.deleteByCronJobIdAndTeamAccount(cronJobId, account);
    }


}
