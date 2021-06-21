package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.exception.CronJobNotFoundException;
import gabia.cronMonitoring.exception.CronProcessNotFoundException;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CronProcessService {

    private final CronProcessRepository cronProcessRepository;
    private final CronJobRepository cronJobRepository;

    @Autowired
    public CronProcessService(CronProcessRepository cronProcessRepository,
        CronJobRepository cronJobRepository) {

        this.cronProcessRepository = cronProcessRepository;
        this.cronJobRepository = cronJobRepository;
    }

    public List<CronProcessDto.Response> findAllCronProcess(UUID cronJobId) {

        List<CronProcessDto.Response> response = new ArrayList<>();

        response = cronProcessRepository.findAllByCronJob_Id(cronJobId).stream()
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

        CronProcess savedCronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        CronProcessDto.Response response = CronProcessDto.Response.from(savedCronProcess);

        return response;
    }

    @Transactional
    public CronProcessDto.Response changeCronProcess(String pid, CronProcessDto.Request request) {

        CronProcess cronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        cronProcess.changeEndTime(request.getEndTime());
        CronProcess savedCronProcess = cronProcessRepository.save(cronProcess);
        CronProcessDto.Response response = CronProcessDto.Response.from(savedCronProcess);

        return response;
    }

}
