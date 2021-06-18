package gabia.cronMonitoring.service.exception;

public class CronServerNotFoundException extends RuntimeException{
    public CronServerNotFoundException(String message){
        super(message);
    }
}
