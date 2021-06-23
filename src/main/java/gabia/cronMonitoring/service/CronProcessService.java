package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.CronLogDto;
import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.process.CronProcessNotFoundException;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronLogRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CronProcessService {

    private final CronProcessRepository cronProcessRepository;
    private final CronJobRepository cronJobRepository;
    private final CronLogRepository cronLogRepository;

    public List<CronProcessDto.Response> findAllCronProcess(UUID cronJobId) {

        List<CronProcessDto.Response> response = cronProcessRepository
            .findAllByCronJob_Id(cronJobId).stream()
            .map(dto -> CronProcessDto.Response.from(dto))
            .collect(Collectors.toList());

        return response;
    }

    @Transactional
    public CronProcessDto.Response makeCronProcess(UUID cronJobId, CronProcessDto.Request request) {

        CronJob cronJob = cronJobRepository.findById(cronJobId)
            .orElseThrow(() -> new CronJobNotFoundException());

        CronProcess cronProcess = CronProcess.builder()
            .pid(request.getPid())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .cronJob(cronJob)
            .build();
        CronProcess savedCronProcess = cronProcessRepository.save(cronProcess);

        CronProcessDto.Response response = CronProcessDto.Response.from(savedCronProcess);

        return response;
    }

    public CronProcessDto.Response findCronProcess(String pid) {

        CronProcess cronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        CronProcessDto.Response response = CronProcessDto.Response.from(cronProcess);

        return response;
    }

    @Transactional
    public CronProcessDto.Response changeCronProcess(String pid, CronProcessDto.Request request) {

        CronProcess cronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        cronProcess.changeEndTime(request.getEndTime());
        CronProcess changedCronProcess = cronProcessRepository.save(cronProcess);
        CronProcessDto.Response response = CronProcessDto.Response.from(changedCronProcess);

        return response;
    }

    public List<CronLogDto.Response> findCronLogs(String pid) {

        List<CronLogDto.Response> response = cronLogRepository.findByTag(pid).stream()
            .map(dto -> CronLogDto.Response.from(dto))
            .collect(Collectors.toList());
        return response;

    }
}
