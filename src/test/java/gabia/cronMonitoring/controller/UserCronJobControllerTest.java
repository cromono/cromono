package gabia.cronMonitoring.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO.Request;
import gabia.cronMonitoring.exception.cron.handler.ControllerExceptionHandler;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.user.UserNotFoundException;
import gabia.cronMonitoring.service.UserCronJobService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(UserCronJobController.class)
public class UserCronJobControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    UserCronJobService userCronJobService;

    @InjectMocks
    ControllerExceptionHandler controllerExceptionHandler;

    @InjectMocks
    UserCronJobController userCronJobController;

    @Before
    public void setUpMockMvc() {
        userCronJobController = new UserCronJobController(userCronJobService);
        mvc = standaloneSetup(userCronJobController)
            .setControllerAdvice(controllerExceptionHandler)
            .build();
    }

    @Test
    public void 모든_유저_크론잡_조회() throws Exception {

        //given
        List<UserCronJobDTO.Response> allResponse = new ArrayList<>();

        UserCronJobDTO.Response testResponse = new UserCronJobDTO.Response();
        testResponse.setUserAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        UserCronJobDTO.Response testResponse2 = new UserCronJobDTO.Response();
        testResponse2.setUserAccount("test");
        testResponse2.setCronJobId(UUID.randomUUID());

        allResponse.add(testResponse);
        allResponse.add(testResponse2);

        //when

        given(userCronJobService.findAllUserCronJob("test")).willReturn(allResponse);

        //then
        mvc.perform(get("/cron-read-auths/users/{userId}/crons/", "test"))
            .andDo(print())
            .andExpect(jsonPath("$[0].userAccount", "test").exists())
            .andExpect(jsonPath("$[1].userAccount", "test").exists())
            .andExpect(jsonPath("$[0].cronJobId", testResponse.getCronJobId()).exists())
            .andExpect(jsonPath("$[1].cronJobId", testResponse2.getCronJobId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 유저_크론잡_추가() throws Exception {

        //given

        UserCronJobDTO.Response testResponse = new UserCronJobDTO.Response();
        testResponse.setUserAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when
        UserCronJobDTO.Request request = new Request();
        request.setCronJobId(UUID.randomUUID());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(userCronJobService.addUserCronJob("test", request))
            .willReturn(testResponse);

        //then
        mvc.perform(post("/cron-read-auths/users/{userId}/crons/", "test")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.userAccount", "test").exists())
            .andExpect(jsonPath("$.cronJobId", testResponse.getCronJobId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 유저_크론잡_추가_크론잡이_없는_경우() throws Exception {

        //given
        UserCronJobDTO.Response testResponse = new UserCronJobDTO.Response();
        testResponse.setUserAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when
        UserCronJobDTO.Request request = new UserCronJobDTO.Request();
        request.setCronJobId(UUID.randomUUID());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(userCronJobService.addUserCronJob("test", request))
            .willThrow(new CronJobNotFoundException());

        //then
        mvc.perform(post("/cron-read-auths/users/{userId}/crons/", "test")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.errorMsg", "Do not find Cron Job").exists())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 유저_크론잡_추가_유저가_없는_경우() throws Exception {

        //given
        UserCronJobDTO.Response testResponse = new UserCronJobDTO.Response();
        testResponse.setUserAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when
        UserCronJobDTO.Request request = new UserCronJobDTO.Request();
        request.setCronJobId(UUID.randomUUID());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(userCronJobService.addUserCronJob("test", request))
            .willThrow(new UserNotFoundException());

        //then
        mvc.perform(post("/cron-read-auths/users/{userId}/crons/", "test")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.errorMsg", "Do not find User").exists())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 유저_크론잡_삭제() throws Exception {

        //given
        UserCronJobDTO.Response testResponse = new UserCronJobDTO.Response();
        testResponse.setUserAccount("test");
        testResponse.setCronJobId(UUID.randomUUID());

        //when

        //then
        mvc.perform(delete("/cron-read-auths/users/{userId}/crons/{cronJobId}", "test",
            testResponse.getCronJobId()))
            .andDo(print())
            .andExpect(status().isOk());
    }


}