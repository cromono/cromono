package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.UserCronJob;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.process.UserNotFoundException;
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

    public List<Response> findAllUserCronJob(String account) {

        List<UserCronJob> test = userCronJobRepository.findByUserAccount(account);
        List<UserCronJobDTO.Response> responses = userCronJobRepository.findByUserAccount(account)
            .stream()
            .map(dto -> UserCronJobDTO.Response.from(dto))
            .collect(Collectors.toList());

        return responses;

    }

    @Transactional
    public UserCronJobDTO.Response addUserCronJob(String account, UUID cronJobId) {

        CronJob cronJob = cronJobRepository.findById(cronJobId)
            .orElseThrow(() -> new CronJobNotFoundException());

        User user = userRepository.findByAccount(account)
            .orElseThrow(() -> new UserNotFoundException());

        UserCronJob userCronJob = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob)
            .build();

        UserCronJob savedUserCronJob = userCronJobRepository.save(userCronJob);

        Response response = Response.from(savedUserCronJob);

        return response;
    }

    @Transactional
    public void removeUserCronJob(String account, UUID cronJobId) {
        CronJob cronJob = cronJobRepository.findById(cronJobId)
            .orElseThrow(() -> new CronJobNotFoundException());

        User user = userRepository.findByAccount(account)
            .orElseThrow(() -> new UserNotFoundException());

        userCronJobRepository.deleteByCronJobIdAndUserAccount(cronJobId, account);
    }


}
