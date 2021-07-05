package gabia.cronMonitoring.exception.cron.server.handler;

import gabia.cronMonitoring.exception.cron.server.AlreadyRegisteredServerException;
import gabia.cronMonitoring.exception.cron.server.CronServerNotFoundException;
import gabia.cronMonitoring.exception.cron.server.NotExistingServerException;
import gabia.cronMonitoring.exception.cron.server.NotValidIPException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServerControllerExceptionHandler {

    @ExceptionHandler(AlreadyRegisteredServerException.class)
    public ResponseEntity handle(AlreadyRegisteredServerException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CronServerNotFoundException.class)
    public ResponseEntity handle(CronServerNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotExistingServerException.class)
    public ResponseEntity handle(NotExistingServerException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidIPException.class)
    public ResponseEntity handle(NotValidIPException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
