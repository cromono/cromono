package gabia.cronMonitoring.service;

import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.service.exception.CronJobExistedException;
import gabia.cronMonitoring.service.exception.CronJobNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CronJobService {

    final boolean DELETE_SUCCESS = true;
    final boolean DELETE_FAILED = false;

    private final CronJobRepository cronJobRepository;


    public CronJob createCronJob(CronJob cronJob) {

        if (cronJobRepository.findById(cronJob.getId()).isPresent()) {
            throw new CronJobExistedException("UUID 중복 - 이미 생성된 크론 잡 입니다");
        }

        return cronJobRepository.save(cronJob);

    }

    public List<CronJob> readCronJobListByServer(String cronServerIp) {
        List<CronJob> cronJobLIst = cronJobRepository.findByServer(cronServerIp);
        return cronJobLIst;
    }

    @Transactional
    public CronJob updateCronJob(UUID cronJobId, CronServer cronServer, String cronName,
        String cronExpr, Date minStartTime, Date maxEndTime) {
        Optional<CronJob> cronJobOptional = cronJobRepository.findById(cronJobId);
        if (cronJobOptional.isEmpty()) {
            throw new CronJobNotFoundException("존재하지 않는 크론 잡 입니다.");
        }
        CronJob cronJob = cronJobOptional.get();
        cronJob.setCronName(cronName);
        cronJob.setCronExpr(cronExpr);
        cronJob.setMinStartTime(minStartTime);
        cronJob.setMaxEndTime(maxEndTime);
        cronJob.setServer(cronServer);
        return cronJob;
    }

    @Transactional
    public boolean deleteCronJob(UUID cronJobId) {

        if (cronJobRepository.findById(cronJobId).isEmpty()) {
            throw new CronJobNotFoundException("존재하지 않는 크론 잡 입니다");
        }

        cronJobRepository.deleteById(cronJobId).get();
        if (cronJobRepository.findById(cronJobId).isPresent()) {
            return DELETE_SUCCESS;
        } else {
            return DELETE_FAILED;
        }

    }

}
