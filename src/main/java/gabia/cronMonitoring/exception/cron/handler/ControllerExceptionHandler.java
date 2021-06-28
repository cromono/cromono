package gabia.cronMonitoring.exception.cron.handler;

import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.dto.CronProcessDto.ErrorResponse;
import gabia.cronMonitoring.dto.TeamCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.process.CronProcessNotFoundException;
import gabia.cronMonitoring.exception.cron.team.TeamNotFoundException;
import gabia.cronMonitoring.exception.cron.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(CronJobNotFoundException.class)
    public ResponseEntity<ErrorResponse> noCronJob(CronJobNotFoundException e) {

        return new ResponseEntity<>(new CronProcessDto.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CronProcessNotFoundException.class)
    public ResponseEntity<CronProcessDto.ErrorResponse> noCronProcess(
        CronProcessNotFoundException e) {

        return new ResponseEntity<>(new CronProcessDto.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserCronJobDTO.ErrorResponse> noUser(UserNotFoundException e) {

        return new ResponseEntity<>(new UserCronJobDTO.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<TeamCronJobDTO.ErrorResponse> noUser(TeamNotFoundException e) {

        return new ResponseEntity<>(new TeamCronJobDTO.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_FOUND);
    }
}
