package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.dto.CronProcessDto.Response;
import gabia.cronMonitoring.service.CronProcessService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process")
public class CronProcessController {

    private final CronProcessService cronProcessService;

    @GetMapping(path = "/")
    public ResponseEntity<List<CronProcessDto.Response>> getCronProcessList(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId) {

        List<Response> allCronProcess = cronProcessService.findAllCronProcess(cronJobId);
        return new ResponseEntity<>(allCronProcess, HttpStatus.OK);
    }

    @PostMapping(path = "/")
    public ResponseEntity<CronProcessDto.Response> createCronProcess(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId,
        @RequestBody CronProcessDto.Request request) {

        Response cronProcess = cronProcessService.makeCronProcess(cronJobId, request);
        return new ResponseEntity<>(cronProcess, HttpStatus.OK);
    }

    @GetMapping(path = "/{pid}")
    public ResponseEntity<CronProcessDto.Response> getCronProcess(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId, @PathVariable(name = "pid") String pid) {

        Response cronProcess = cronProcessService.findCronProcess(pid);
        return new ResponseEntity<>(cronProcess, HttpStatus.OK);
    }

    @PatchMapping(path = "/{pid}")
    public ResponseEntity<CronProcessDto.Response> updateCronProcess(
        @PathVariable(name = "serverIp") String serverIp,
        @PathVariable(name = "cronJobId") UUID cronJobId, @PathVariable(name = "pid") String pid,
        @RequestBody CronProcessDto.Request request) {

        Response cronProcess = cronProcessService.changeCronProcess(pid, request);
        return new ResponseEntity<>(cronProcess, HttpStatus.OK);

    }

}
