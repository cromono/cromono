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

    public List<CronProcessDto.Response> findAllCronProcess(String serverIp, UUID cronJobId) {

        List<CronProcessDto.Response> response = new ArrayList<>();

//        response = cronProcessRepository.findAllByCronJob_Id(cronJobId).stream()
//            .map(dto -> new CronProcessDto.Response(dto.getCronJob().getId(),
//                dto.getPid(), dto.getStartTime(), dto.getEndTime()))
//            .collect(Collectors.toList());

        response = cronProcessRepository.findAllByCronJob_Id(cronJobId).stream()
            .map(dto -> CronProcessDto.Response.from(dto))
            .collect(Collectors.toList());

        return response;
    }

    public CronProcessDto.Response makeCronProcess(String serverIp, UUID cronJobId,
        CronProcessDto.Request request) {

//        CronProcessDto.Response response = new CronProcessDto.Response();
        CronJob cronJob = cronJobRepository.findById(cronJobId)
            .orElseThrow(() -> new CronJobNotFoundException());

        CronProcess cronProcess = new CronProcess();
        cronProcess.setPid(request.getPid());
        cronProcess.setStartTime(request.getStartTime());
        cronProcess.setEndTime(request.getEndTime());
        cronProcess.setCronJob(cronJob);
        CronProcess savedCronProcess = cronProcessRepository.save(cronProcess);

        CronProcessDto.Response response = CronProcessDto.Response.from(savedCronProcess);
//        response = changeEntityToDto(savedCronProcess);

        return response;
    }

    public CronProcessDto.Response findCronProcess(String serverIp, UUID cronJobId, String pid) {

//        CronProcessDto.Response response = new CronProcessDto.Response();
        CronProcess savedCronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        CronProcessDto.Response response = CronProcessDto.Response.from(savedCronProcess);
//        response = changeEntityToDto(cronProcess);

        return response;
    }

    public CronProcessDto.Response changeCronProcess(String serverIp, UUID cronJobId, String pid,
        CronProcessDto.Request request) {

        CronProcess cronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        cronProcess.setEndTime(request.getEndTime());
        CronProcess savedCronProcess = cronProcessRepository.save(cronProcess);
        CronProcessDto.Response response = CronProcessDto.Response.from(savedCronProcess);

        return response;
    }

}
