package gabia.cronMonitoring.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CronMonitorUtil {

    public static  <T> String objToJson(T obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(obj);
    }

    public static <T> T jsonStrToObj(String jsonStr, Class<T> classObj)
        throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonStr,classObj);
    }

}

