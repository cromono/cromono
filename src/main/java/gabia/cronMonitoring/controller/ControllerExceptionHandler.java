package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.dto.CronProcessDto.ErrorResponse;
import gabia.cronMonitoring.exception.CronJobNotFoundException;
import gabia.cronMonitoring.exception.CronProcessNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(CronJobNotFoundException.class)
    public ResponseEntity<ErrorResponse> noCronJob(CronJobNotFoundException e) {

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
}
