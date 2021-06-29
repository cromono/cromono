package gabia.cronMonitoring.exception.teamUser.handler;

import gabia.cronMonitoring.dto.TeamDTO;
import gabia.cronMonitoring.dto.TeamDTO.ErrorResponse;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.teamUser.AuthException;
import gabia.cronMonitoring.exception.teamUser.NotTeamMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TeamUserExceptionHandler {

    @ExceptionHandler(CronJobNotFoundException.class)
    public ResponseEntity<ErrorResponse> notHaveAuth(AuthException e) {
        return new ResponseEntity<>(new TeamDTO.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(NotTeamMemberException.class)
    public ResponseEntity<ErrorResponse> noTeamMember(NotTeamMemberException e) {
        return new ResponseEntity<>(new TeamDTO.ErrorResponse(e.getMessage()),
            HttpStatus.NOT_ACCEPTABLE);
    }
}
