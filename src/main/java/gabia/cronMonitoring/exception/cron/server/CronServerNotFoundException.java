package gabia.cronMonitoring.exception.cron.server;

public class CronServerNotFoundException extends RuntimeException{
    public CronServerNotFoundException(String message){
        super(message);
    }
}
