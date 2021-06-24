package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO.Response;
import gabia.cronMonitoring.entity.UserCronJob;
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.UserCronJobRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserCronJobService {

//    private final CronJobRepositoryDataJpa cronJobRepository;
    private final UserCronJobRepository userCronJobRepository;

    public List<Response> findAllUserCronJob(String account) {

        List<UserCronJob> test = userCronJobRepository.findByUserAccount(account);
        List<UserCronJobDTO.Response> responses = userCronJobRepository.findByUserAccount(account)
            .stream()
            .map(dto -> UserCronJobDTO.Response.from(dto))
            .collect(Collectors.toList());

        return responses;

    }

}
