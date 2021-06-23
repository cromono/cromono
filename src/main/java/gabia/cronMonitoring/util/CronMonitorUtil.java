package gabia.cronMonitoring.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CronMonitorUtil {

    private static  ObjectMapper mapper;

    @Autowired
    private CronMonitorUtil(ObjectMapper mapper){
        this.mapper=mapper;
    }

    public static  <T> String objToJson(T obj) throws JsonProcessingException {
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(obj);
    }

    public static <T> T jsonStrToObj(String jsonStr, Class<T> classObj)
        throws JsonProcessingException {
        return mapper.readValue(jsonStr,classObj);
    }

}

