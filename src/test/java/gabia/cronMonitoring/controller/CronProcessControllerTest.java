package gabia.cronMonitoring.controller;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import gabia.cronMonitoring.dto.CronProcessDto;
import gabia.cronMonitoring.dto.CronProcessDto.Request;
import gabia.cronMonitoring.dto.CronProcessDto.Response;
import gabia.cronMonitoring.entity.CronProcess;
import gabia.cronMonitoring.service.CronProcessService;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.annotations.NaturalId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(CronProcessController.class)
public class CronProcessControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    CronProcessService cronProcessService;

    @InjectMocks
    CronProcessController cronProcessController;


    @Before
    public void setUpMockMvc() {
        cronProcessController = new CronProcessController(cronProcessService);
        mvc = MockMvcBuilders.standaloneSetup(cronProcessController).build();
    }

    @Test
    public void 모든_크론_프로세스_조회() throws Exception {

        //given
        List<Response> allResponse = new ArrayList<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronProcessDto.Response testResponse = new CronProcessDto.Response();
        testResponse.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        testResponse.setPid("12");
        testResponse.setStartTime(timestamp);

        CronProcessDto.Response testResponse2 = new CronProcessDto.Response();
        testResponse2.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440002"));
        testResponse2.setPid("15");
        testResponse2.setStartTime(timestamp);

        allResponse.add(testResponse);
        allResponse.add(testResponse2);

        //when
        BDDMockito.given(cronProcessService.findAllCronProcess("0.0.0.0",
            UUID.fromString("123e4567-e89b-12d3-a456-556642440000"))).willReturn(allResponse);

        //then
        mvc.perform(
            MockMvcRequestBuilders
                .get("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/", "0.0.0.0",
                    UUID.fromString("123e4567-e89b-12d3-a456-556642440000")))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].pid").value("12"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].cronJobId")
                .value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].pid").value("15"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].cronJobId")
                .value("123e4567-e89b-12d3-a456-556642440002"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void createCronProcess() throws Exception {
        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        CronProcessDto.Response response = new CronProcessDto.Response();
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        response.setPid("12");
        response.setStartTime(timestamp);

        //when
        CronProcessDto.Request request = new CronProcessDto.Request();
        request.setPid("12");
        request.setStartTime(timestamp);
        request.setEndTime(timestamp);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        BDDMockito.given(cronProcessService.makeCronProcess(any(),
            any(), any())).willReturn(response);

//        BDDMockito.given(cronProcessService.makeCronProcess("0.0.0.0",
//            UUID.fromString("123e4567-e89b-12d3-a456-556642440000"),
//            request)).will(invocation -> {
//                CronProcessDto.Response response1 = invocation.getArgument(0);
//                response1.setPid("12");
//                response1.setStartTime(timestamp);
//                return response1;
//        });


        //then
        mvc.perform(MockMvcRequestBuilders
            .post("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/", "0.0.0.0",
                "123e4567-e89b-12d3-a456-556642440000")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(requestJson)

        )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.pid").value("12"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cronJobId")
                .value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCronProcess() throws Exception {
        //given
        CronProcessDto.Response response = new CronProcessDto.Response();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        response.setPid("12");
        response.setStartTime(timestamp);

        //when
        BDDMockito.given(cronProcessService.findCronProcess("0.0.0.0",
            UUID.fromString("123e4567-e89b-12d3-a456-556642440000"), "12")).willReturn(response);

        //then
        mvc.perform(
            MockMvcRequestBuilders
                .get("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/{pid}", "0.0.0.0",
                    UUID.fromString("123e4567-e89b-12d3-a456-556642440000"), "12"))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.pid").value("12"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cronJobId")
                .value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void updateCronProcess() throws Exception {
        //given
        CronProcessDto.Response response = new CronProcessDto.Response();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        response.setPid("12");

        //when
        CronProcessDto.Request request = new Request();
        request.setPid("12");
//        BDDMockito.given(cronProcessService.changeCronProcess("0.0.0.0",
//            UUID.fromString("123e4567-e89b-12d3-a456-556642440000"), "12", any()))
//            .willReturn(response);

        BDDMockito.given(cronProcessService.changeCronProcess(any(),any(),any(),any()))
            .willReturn(response);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);


        //then
        mvc.perform(
            MockMvcRequestBuilders
                .patch("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/{pid}", "0.0.0.0",
                    UUID.fromString("123e4567-e89b-12d3-a456-556642440000"), "12")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson))
            .andDo(print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.pid").value("12"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cronJobId")
                .value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

}