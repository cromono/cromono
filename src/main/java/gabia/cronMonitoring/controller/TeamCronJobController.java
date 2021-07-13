package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.TeamCronJobDTO;
import gabia.cronMonitoring.dto.TeamCronJobDTO.Response;
import gabia.cronMonitoring.service.TeamCronJobService;
import gabia.cronMonitoring.util.ValidUUID;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/cron-read-auths/teams/{teamId}/crons")
@CrossOrigin(origins = "*")
public class TeamCronJobController {

    private final TeamCronJobService teamCronJobService;

    @GetMapping(path = "/")
    public ResponseEntity<List<TeamCronJobDTO.Response>> getTeamCronJob(
        @NotEmpty @PathVariable(value = "teamId") String teamId) {

        List<Response> allTeamCronJob = teamCronJobService
            .findAllTeamCronJob(teamId);
        return new ResponseEntity<>(allTeamCronJob, HttpStatus.OK);

    }

    @PostMapping(path = "/")
    public ResponseEntity<TeamCronJobDTO.Response> postTeamCronJob(
        @NotEmpty @PathVariable(value = "teamId") String teamId,
        @RequestBody @Valid TeamCronJobDTO.Request request) {

        TeamCronJobDTO.Response response = teamCronJobService.addTeamCronJob(teamId, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{cronJobId}")
    public ResponseEntity<HttpStatus> deleteTeamCronJob(
        @NotEmpty @PathVariable(value = "teamId") String teamId,
        @ValidUUID @PathVariable(value = "cronJobId") UUID cronJobId) {

        teamCronJobService.removeTeamCronJob(teamId, cronJobId);

        return new ResponseEntity(HttpStatus.OK);

    }

}
