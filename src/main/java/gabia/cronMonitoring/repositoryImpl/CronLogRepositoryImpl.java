package gabia.cronMonitoring.repositoryImpl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import gabia.cronMonitoring.repository.CronLogRepository;
import gabia.cronMonitoring.entity.CronLog;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CronLogRepositoryImpl implements CronLogRepository {

//        private static final String server = "http://182.162.142.151:8086/"; // 공인 IP
    private static final String server = "http://10.7.27.9:8086/"; // 사설 IP
    private static final char[] token =
        "W90KOru9HsUcsbJ-7NDZvl-ECE5OdsKe3F8LsuNlY5pNQr9mtrE887RnARrNP1Jr6MgE3BACeXptTOkp6E5ibQ=="
            .toCharArray();
    private static final String org = "Gabia";
    private static final String bucket = "Cron";

    private InfluxDBClient influxDBClient;

    @Override
    public List<CronLog> findByTag(String cronProcess) {

        influxDBClient = InfluxDBClientFactory.create(server, token, org, bucket);

        StringBuilder stringBuilder = new StringBuilder();

        String from = "from(bucket: \"Cron\")\n";
        //  TODO: 기간 제약으로 인해 테스트 코드가 작동하는 기한에 제약이 생김
        String range = "|> range(start: -60d)";
        stringBuilder.append("|> filter(fn: (r) => (r[\"_measurement\"] == \"cron_log\" ");
        stringBuilder.append("and r[\"_field\"] == \"log\" ");
        stringBuilder.append("and r[\"cronProcess\"] ");
        stringBuilder.append("== \"");
        stringBuilder.append(cronProcess);
        stringBuilder.append("\"))");
        String filter = stringBuilder.toString();

        String flux = from + range + filter;

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);

        List<CronLog> cronLogs = new LinkedList<>();

        for (FluxTable table : tables) {
            List<FluxRecord> records = table.getRecords();
            for (FluxRecord record : records) {
                CronLog cronLog =
                    new CronLog(record.getTime(), cronProcess, record.getStart(), record.getStop(),
                        record.getValueByKey("_value").toString());
                cronLogs.add(cronLog);
            }
        }

        influxDBClient.close();

        return cronLogs;
    }


}
