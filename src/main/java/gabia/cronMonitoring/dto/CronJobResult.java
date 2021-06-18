package gabia.cronMonitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CronJobResult<T> {

    private T data;
}