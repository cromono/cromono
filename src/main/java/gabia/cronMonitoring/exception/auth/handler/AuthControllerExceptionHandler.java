package gabia.cronMonitoring.exception.auth.handler;

import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthControllerExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity handle(InvalidTokenException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
