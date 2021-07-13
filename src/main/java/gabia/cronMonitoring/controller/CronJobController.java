package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.dto.CronJobResult;
import gabia.cronMonitoring.service.CronJobService;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CronJobController {

    public final CronJobService cronJobService;


    //크론서버별크론잡목록조회
    @GetMapping(value = "/cron-servers/{serverIp}/cron-jobs")
    public CronJobResult findCronJobByServer(@PathVariable("serverIp") @NotEmpty String serverIp) {
        List<CronJobDTO> cronJobDTOs = cronJobService.readCronJobListByServer(serverIp);
        return new CronJobResult(cronJobDTOs);
    }

    //크론 job 등록
    @PostMapping(value = "/cron-servers/{serverIp}/cron-jobs")
    public CronJobResult createCronJob(@RequestBody CronJobDTO cronJobDTO,
        @PathVariable("serverIp") @NotEmpty String serverIp) {
        cronJobDTO.setServerIp(serverIp);
        CronJobDTO createdJob = cronJobService.createCronJob(cronJobDTO);
        return new CronJobResult(createdJob.getId());
    }

    //크론 job 수정
    @PatchMapping(value = "/cron-servers/{serverIp}/cron-jobs/{cronJobId}")
    public CronJobResult updateCronJob(@RequestBody CronJobDTO cronJobDTO,
        @PathVariable("serverIp") @NotEmpty String serverIp,
        @PathVariable("cronJobId") @NotEmpty String cronJobId) {
        CronJobDTO updateCronJobDTO = cronJobService
            .updateCronJob(UUID.fromString(cronJobId), serverIp, cronJobDTO.getCronName(),
                cronJobDTO.getCronExpr(), cronJobDTO.getMinStartTime(), cronJobDTO.getMaxEndTime());
        return new CronJobResult(updateCronJobDTO.getId().toString());

    }

    //크론 job 삭제
    @DeleteMapping(value = "/cron-servers/{serverIp}/cron-jobs/{cronJobId}")
    public ResponseEntity<?> deleteCronJob(
        @PathVariable("serverIp") @NotEmpty String serverIp,
        @PathVariable("cronJobId") @NotEmpty String cronJobId) {
        boolean ret = cronJobService.deleteCronJob(UUID.fromString(cronJobId));
        if (ret) {
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
