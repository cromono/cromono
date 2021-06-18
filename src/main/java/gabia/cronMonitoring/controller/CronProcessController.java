package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.dto.CronProcessDto.Response;
import gabia.cronMonitoring.exception.CronJobNotFoundException;
import gabia.cronMonitoring.exception.CronProcessNotFoundException;
import gabia.cronMonitoring.service.CronProcessService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process")
public class CronProcessController {

    CronProcessService cronProcessService;

    @Autowired
    public CronProcessController(CronProcessService cronProcessService) {

        this.cronProcessService = cronProcessService;
    }

    //크론 프로세스 목록 조회
    @GetMapping(path = "/")
    public ResponseEntity<List<CronProcessDto.Response>> getCronProcessList(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId) {

        List<Response> allCronProcess = cronProcessService.findAllCronProcess(serverIp, cronJobId);
        return new ResponseEntity<>(allCronProcess, HttpStatus.OK);
    }

    //크론 프로세스 생성
    @PostMapping(path = "/")
    public ResponseEntity<CronProcessDto.Response> createCronProcess(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId,
        @RequestBody CronProcessDto.Request request) {

        Response cronProcess = cronProcessService.makeCronProcess(serverIp, cronJobId, request);
        return new ResponseEntity<>(cronProcess, HttpStatus.OK);
    }

    //크론 프로세스 상세 조회
    @GetMapping(path = "/{pid}")
    public ResponseEntity<CronProcessDto.Response> getCronProcess(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId, @PathVariable(name = "pid") String pid) {

        Response cronProcess = cronProcessService.findCronProcess(serverIp, cronJobId, pid);
        return new ResponseEntity<>(cronProcess, HttpStatus.OK);
    }

    //크론 프로세스 업데이트
    @PatchMapping(path = "/{pid}")
    public ResponseEntity<CronProcessDto.Response> updateCronProcess(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId, @PathVariable(name = "pid") String pid,
        @RequestBody CronProcessDto.Request request) {

        Response cronProcess = cronProcessService
            .changeCronProcess(serverIp, cronJobId, pid, request);
        return new ResponseEntity<>(cronProcess, HttpStatus.OK);

    }

    @ExceptionHandler(CronJobNotFoundException.class)
    public ResponseEntity<CronProcessDto.ErrorResponse> noCronJob(CronJobNotFoundException e) {

        return new ResponseEntity<>(
            new CronProcessDto.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(CronProcessNotFoundException.class)
    public ResponseEntity<CronProcessDto.ErrorResponse> noCronProcess(
        CronProcessNotFoundException e) {

        return new ResponseEntity<>(new CronProcessDto.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_FOUND);
    }

//    //프로세스 로그 목록 조회
//    public ResponseEntity<>

}
