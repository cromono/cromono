package gabia.cronMonitoring.exception.cron.job;

public class CronServerNotFoundException extends RuntimeException{
    public CronServerNotFoundException(String message){
        super(message);
    }
}
