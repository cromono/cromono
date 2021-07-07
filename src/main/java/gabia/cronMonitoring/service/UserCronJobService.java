package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.UserCronJob;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.team.TeamNotFoundException;
import gabia.cronMonitoring.exception.cron.user.UserNotFoundException;
import gabia.cronMonitoring.exception.teamcronjob.AlreadyExistTeamCronJobException;
import gabia.cronMonitoring.exception.usercronjob.AlreadyExistUserCronJobException;
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.UserCronJobRepository;
import gabia.cronMonitoring.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserCronJobService {

    private final CronJobRepositoryDataJpa cronJobRepository;
    private final UserCronJobRepository userCronJobRepository;
    private final UserRepository userRepository;

    /**
     * User Cron Job 조회
     *
     * @param account 해당 유저의 account
     * @return List<UserCronJobDTO.Response></UserCronJobDTO.Response>
     */
    public List<Response> findAllUserCronJob(String account) {

        // 해당 하는 User의 User Cron Job List 조회
        List<UserCronJobDTO.Response> responses = userCronJobRepository.findByUserAccount(account)
            .stream()
            .map(dto -> UserCronJobDTO.Response.from(dto))
            .collect(Collectors.toList());

        return responses;

    }

    /**
     * User Cron Job 추가
     *
     * @param account 해당 유저의 account
     * @param request UserCronJobDTO.Request
     * @return UserCronJobDTO.Response
     * @throws CronJobNotFoundException
     * @throws UserNotFoundException
     * @throws AlreadyExistUserCronJobException
     */
    @Transactional
    public UserCronJobDTO.Response addUserCronJob(String account, UserCronJobDTO.Request request) {

        // Cron Job과 User 존재 유무 확인
        CronJob cronJob = cronJobRepository.findById(request.getCronJobId())
            .orElseThrow(() -> new CronJobNotFoundException());

        User user = userRepository.findByAccount(account)
            .orElseThrow(() -> new UserNotFoundException());

        // 이미 존재하는 User Cron Job인 경우 Exception 발생
        userCronJobRepository.findByUserAccountAndCronJobId(account, request.getCronJobId())
            .ifPresent(present -> {
                throw new AlreadyExistUserCronJobException();
            });

        // User Cron Job 생성
        UserCronJob userCronJob = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob)
            .build();
        UserCronJob savedUserCronJob = userCronJobRepository.save(userCronJob);

        Response response = Response.from(savedUserCronJob);

        return response;
    }

    /**
     * User Cron Job 삭제
     *
     * @param account   해당 유저의 account
     * @param cronJobId 삭제할 User Cron Job의 Id
     * @throws CronJobNotFoundException
     * @throws UserNotFoundException
     */
    @Transactional
    public void removeUserCronJob(String account, UUID cronJobId) {

        // Cron Job, User 존재 유무 확인
        cronJobRepository.findById(cronJobId).orElseThrow(() -> new CronJobNotFoundException());
        userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException());

        // User Cron Job 삭제
        userCronJobRepository.deleteByCronJobIdAndUserAccount(cronJobId, account);
    }
}
