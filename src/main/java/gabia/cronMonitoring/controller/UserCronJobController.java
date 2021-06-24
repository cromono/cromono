package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO.Response;
import gabia.cronMonitoring.service.UserCronJobService;
import java.util.List;
import java.util.UUID;
import javax.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/cron-read-auths/users/{userId}/crons")
public class UserCronJobController {

    private final UserCronJobService userCronJobService;

    @GetMapping(path = "/")
    public ResponseEntity<List<UserCronJobDTO.Response>> getUserCronJob(
        @PathVariable(value = "userId") String userId) {

        List<UserCronJobDTO.Response> allUserCronJob = userCronJobService
            .findAllUserCronJob(userId);
        return new ResponseEntity<>(allUserCronJob, HttpStatus.OK);

    }

    @PostMapping(path = "/{cronJobId}")
    public ResponseEntity<UserCronJobDTO.Response> postUserCronJob(
        @PathVariable(value = "userId") String userId,
        @PathVariable(value = "cronJobId") UUID cronJobId) {

        Response response = userCronJobService.addUserCronJob(userId, cronJobId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{cronJobId}")
    public ResponseEntity<> deleteUserCronJob(
        @PathVariable(value = "userId") String userId,
        @PathVariable(value = "cronJobId") UUID cronJobId) {

        userCronJobService.removeUserCronJob(userId, cronJobId);

        return new ResponseEntity(HttpStatus.OK);

    }

}
