package gabia.cronMonitoring.service;

import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.mapper.CronMapper;
import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.exception.cron.job.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.job.CronServerNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private final CronServerRepository cronServerRepository;
    @Transactional
    public CronJobDTO createCronJob(CronJobDTO cronJobDTO) {
        if (cronServerRepository.findByIp(cronJobDTO.getServerIp()).isEmpty()) {
            throw new CronServerNotFoundException("존재하지 않는 Server IP - 잘못된 IP:"+cronJobDTO.getServerIp());
        }
        CronServer cronServer = cronServerRepository.findByIp(cronJobDTO.getServerIp()).get();
        CronJob cronJob = CronMapper.toCronJobEntity(cronJobDTO,cronServer);
        cronJobRepository.save(cronJob);
        return CronMapper.toCronJobDTO(cronJob);
    }

    public List<CronJobDTO> readCronJobListByServer(String cronServerIp) {
        List<CronJob> cronJobList = cronJobRepository.findByServer(cronServerIp);
        return cronJobList.stream().map(
            cronJob -> CronMapper.toCronJobDTO(cronJob))
            .collect(
                Collectors.toList());
    }

    @Transactional
    public CronJobDTO updateCronJob(UUID cronJobId, String serverIp, String cronName,
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
        cronJob.setServer(cronServerRepository.findByIp(serverIp).get());
        return CronMapper.toCronJobDTO(cronJob);
    }

    @Transactional
    public boolean deleteCronJob(UUID cronJobId) {

        if (cronJobRepository.findById(cronJobId).isEmpty()) {
            throw new CronJobNotFoundException("존재하지 않는 크론 잡 입니다");
        }

        cronJobRepository.deleteById(cronJobId).get();
        if (cronJobRepository.findById(cronJobId).isEmpty()) {
            return DELETE_SUCCESS;
        } else {
            return DELETE_FAILED;
        }

    }

}
