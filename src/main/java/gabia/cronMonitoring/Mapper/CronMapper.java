package gabia.cronMonitoring.Mapper;


import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CronMapper {

    private final CronServerRepository cronServerRepository;

    public CronJob toCronJobEntity(CronJobDTO cronJobDTO){
        CronJob cronJob = new CronJob();
        cronJob.setCronName(cronJobDTO.getCronName());
        cronJob.setCronExpr(cronJobDTO.getCronExpr());
        cronJob.setMaxEndTime(cronJobDTO.getMaxEndTime());
        cronJob.setMinStartTime(cronJobDTO.getMinStartTime());
        cronJob.setServer(cronServerRepository.findByIp(cronJobDTO.getServerIp()).get());
        return cronJob;
    }


}
