package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.CronServerDTO;
import gabia.cronMonitoring.service.CronServerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CronServerController {

    private final CronServerService cronServerService;

    @GetMapping(value = "/cron-servers")
    @ResponseBody
    public ResponseEntity<List<CronServerDTO>> getCronServers() {
        List<CronServerDTO> cronServers = cronServerService.getCronServers();
        ResponseEntity responseEntity = new ResponseEntity(cronServers, HttpStatus.OK);
        return responseEntity;
    }

    @PostMapping(value = "/cron-servers")
    @ResponseBody
    public ResponseEntity<CronServerDTO> postCronServers(@RequestBody CronServerDTO dto) {
        CronServerDTO cronServerDTO = cronServerService.addCronServer(dto.getServerIp());
        ResponseEntity responseEntity = new ResponseEntity(cronServerDTO, HttpStatus.CREATED);
        return responseEntity;
    }

    @PatchMapping("/cron-servers/{serverIp}")
    @ResponseBody
    public ResponseEntity<CronServerDTO> patchCronServer(@PathVariable(name = "serverIp") String serverIp,
        @RequestBody CronServerDTO cronServerDTO) {
        CronServerDTO cronServer = cronServerService
            .updateCronServer(serverIp, cronServerDTO.getServerIp());
        ResponseEntity responseEntity = new ResponseEntity(cronServer, HttpStatus.OK);
        return responseEntity;
    }

    @DeleteMapping(value = "/cron-servers/{serverIp}")
    @ResponseBody
    public ResponseEntity deleteCronServer(@PathVariable(name = "serverIp") String serverIp) {
        cronServerService.deleteCronServer(serverIp);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
        return responseEntity;
    }
}