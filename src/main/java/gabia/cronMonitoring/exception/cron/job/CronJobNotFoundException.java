package gabia.cronMonitoring.exception.cron.job;

public class CronJobNotFoundException extends RuntimeException{
    public CronJobNotFoundException(String message){
        super(message);
    }
}
