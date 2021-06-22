package gabia.cronMonitoring.mapper;


import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CronMapper {

    public static CronJob toCronJobEntity(CronJobDTO cronJobDTO, CronServer cronServer) {
        CronJob cronJob = new CronJob();
        cronJob.setId(cronJobDTO.getId());
        cronJob.setCronName(cronJobDTO.getCronName());
        cronJob.setCronExpr(cronJobDTO.getCronExpr());
        cronJob.setMaxEndTime(cronJobDTO.getMaxEndTime());
        cronJob.setMinStartTime(cronJobDTO.getMinStartTime());
        cronJob.setServer(cronServer);
        return cronJob;
    }

    public static CronJobDTO toCronJobDTO(CronJob cronJob) {
        CronJobDTO cronJobDTO =
            new CronJobDTO(cronJob.getId(), cronJob.getCronName(), cronJob.getCronExpr(),
                cronJob.getMinStartTime(), cronJob.getMaxEndTime(), cronJob.getServer().getIp());

        return cronJobDTO;
    }

}
