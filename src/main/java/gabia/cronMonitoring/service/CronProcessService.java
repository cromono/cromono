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

    /**
     * 해당 Cron Job의 모든 Cron Process List를 조회합니다.
     *
     * @param cronJobId 해당하는 Cron Job의 Id
     * @return List<CronProcessDto.Response></CronProcessDto.Response>
     */
    public List<CronProcessDto.Response> findAllCronProcess(UUID cronJobId) {

        // 모든 Cron Process List 를 조회
        List<CronProcessDto.Response> response = cronProcessRepository
            .findAllByCronJob_Id(cronJobId).stream()
            .map(dto -> CronProcessDto.Response.from(dto))
            .collect(Collectors.toList());

        return response;
    }

    /**
     * Cron Process 를 생성합니다.
     *
     * @param cronJobId 해당하는 Cron Job의 Id
     * @param request   CronProcessDto.Request
     * @return CronProcessDto.Response
     * @throws CronJobNotFoundException
     */
    @Transactional
    public CronProcessDto.Response makeCronProcess(UUID cronJobId, CronProcessDto.Request request) {

        // 해당하는 CronJob이 존재하는지 확인
        CronJob cronJob = cronJobRepository.findById(cronJobId)
            .orElseThrow(() -> new CronJobNotFoundException());

        // Cron Process 생성 후 저장
        CronProcess cronProcess = CronProcess.builder()
            .pid(request.getPid())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .cronJob(cronJob)
            .build();
        CronProcess savedCronProcess = cronProcessRepository.save(cronProcess);

        // Cron Process Entity를 Responose DTO 로 Mapping
        CronProcessDto.Response response = CronProcessDto.Response.from(savedCronProcess);

        return response;
    }

    /**
     * Cron Process 조회
     *
     * @param pid Process의 Pid
     * @return CronProcessDto.Response
     * @throws CronProcessNotFoundException
     */
    public CronProcessDto.Response findCronProcess(String pid) {

        // 해당하는 Cron Process 조회, 없으면 Exception throw
        CronProcess cronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        CronProcessDto.Response response = CronProcessDto.Response.from(cronProcess);

        return response;
    }

    /**
     * Cron Process End Time 수정
     *
     * @param pid     Process의 pid
     * @param request CronProcessDto.Request
     * @return CronProcessDto.Response
     * @throws CronProcessNotFoundException
     */
    @Transactional
    public CronProcessDto.Response changeCronProcess(String pid, CronProcessDto.Request request) {

        // 해당하는 Cron Process 조회, 없으면 Exception throw;
        CronProcess cronProcess = cronProcessRepository.findByPid(pid)
            .orElseThrow(() -> new CronProcessNotFoundException());

        // End Time 수정, Cron Process 는 종료 시에 End Time 만 수정 가능
        cronProcess.changeEndTime(request.getEndTime());
        CronProcess changedCronProcess = cronProcessRepository.save(cronProcess);
        CronProcessDto.Response response = CronProcessDto.Response.from(changedCronProcess);

        return response;
    }

    /**
     * Log 조회
     *
     * @param pid Process의 pid
     * @return List<CronLogDto.Response></CronLogDto.Response>
     */
    public List<CronLogDto.Response> findCronLogs(String pid) {

        // Influx DB에서 해당 pid를 가진 Cron Log 조회
        List<CronLogDto.Response> response = cronLogRepository.findByTag(pid).stream()
            .map(dto -> CronLogDto.Response.from(dto))
            .collect(Collectors.toList());
        return response;

    }
}
