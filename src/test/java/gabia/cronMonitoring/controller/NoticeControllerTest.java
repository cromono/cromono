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
import gabia.cronMonitoring.dto.NoticeDTO;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO.Request;
import gabia.cronMonitoring.dto.NoticeSubscriptionDTO.Response;
import gabia.cronMonitoring.entity.Enum.NoticeType;
import gabia.cronMonitoring.exception.cron.handler.ControllerExceptionHandler;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.user.UserNotFoundException;
import gabia.cronMonitoring.exception.notice.NoticeNotFoundException;
import gabia.cronMonitoring.exception.notice.handler.NoticeControllerExceptionHandler;
import gabia.cronMonitoring.exception.notice.usernotice.AlreadyExistNoticeSubscriptionException;
import gabia.cronMonitoring.service.NoticeService;
import java.sql.Timestamp;
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
@WebMvcTest(NoticeController.class)
public class NoticeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    NoticeService noticeService;

    @InjectMocks
    NoticeControllerExceptionHandler noticeControllerExceptionHandler;

    @InjectMocks
    ControllerExceptionHandler controllerExceptionHandler;

    @InjectMocks
    NoticeController noticeController;

    @Before
    public void setUpMockMvc() {
        noticeController = new NoticeController(noticeService);
        mvc = standaloneSetup(noticeController)
            .setControllerAdvice(controllerExceptionHandler, noticeControllerExceptionHandler)
            .build();
    }

    @Test
    public void 모든_알림_구독_리스트_조회() throws Exception {
        //given
        List<NoticeSubscriptionDTO.Response> allResponse = new ArrayList<>();

        NoticeSubscriptionDTO.Response testResponse = new NoticeSubscriptionDTO.Response();
        testResponse.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        NoticeSubscriptionDTO.Response testResponse2 = new NoticeSubscriptionDTO.Response();
        testResponse2.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440001"));

        allResponse.add(testResponse);
        allResponse.add(testResponse2);

        //when
        given(noticeService.findAllNoticeSubscription("test")).willReturn(allResponse);

        //then
        mvc.perform(get("/notifications/users/{userId}", "test"))
            .andDo(print())
            .andExpect(jsonPath("$[0].cronJobId").value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(jsonPath("$[1].cronJobId").value("123e4567-e89b-12d3-a456-556642440001"))
            .andExpect(status().isOk());
    }

    @Test
    public void 알림_구독_추가() throws Exception {
        //given
        NoticeSubscriptionDTO.Response response = new NoticeSubscriptionDTO.Response();
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCreateUserId("test");
        request.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        request.setRcvUserId("test");

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(noticeService.addNoticeSubscription("test", request)).willReturn(response);

        //then
        mvc.perform(post("/notifications/users/{userId}", "test").content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.cronJobId").value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(status().isOk());
    }

    @Test
    public void 알림_구독_추가_크론잡이_없는_경우() throws Exception {
        //given
        NoticeSubscriptionDTO.Response response = new NoticeSubscriptionDTO.Response();
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCreateUserId("test");
        request.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        request.setRcvUserId("test");

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(noticeService.addNoticeSubscription("test", request))
            .willThrow(new CronJobNotFoundException());

        //then
        mvc.perform(post("/notifications/users/{userId}", "test").content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.errorMsg").value("Do not find Cron Job"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void 알림_구독_추가_유저가_없는_경우() throws Exception {
        //given
        NoticeSubscriptionDTO.Response response = new NoticeSubscriptionDTO.Response();
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCreateUserId("test");
        request.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        request.setRcvUserId("test");

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(noticeService.addNoticeSubscription("test", request))
            .willThrow(new UserNotFoundException());

        //then
        mvc.perform(post("/notifications/users/{userId}", "test").content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.errorMsg").value("Do not find User"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void 알림_구독_추가_알림이_이미_있는_경우() throws Exception {
        //given
        NoticeSubscriptionDTO.Response response = new NoticeSubscriptionDTO.Response();
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //when
        NoticeSubscriptionDTO.Request request = new Request();
        request.setCreateUserId("test");
        request.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        request.setRcvUserId("test");

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        given(noticeService.addNoticeSubscription("test", request))
            .willThrow(new AlreadyExistNoticeSubscriptionException());

        //then
        mvc.perform(post("/notifications/users/{userId}", "test").content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.errorMsg").value("Already exist notice subscription"))
            .andExpect(status().isConflict());
    }

    @Test
    public void 알림_구독_삭제() throws Exception {
        //given
        NoticeSubscriptionDTO.Response response = new Response();
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));

        //when

        //then
        mvc.perform(delete("/notifications/users/{userId}/crons/{cronJobId}", "test",
            response.getCronJobId())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void 알림_리스트_조회_성공() throws Exception {
        //given
        List<NoticeDTO.Response> allResponse = new ArrayList<>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        NoticeDTO.Response testResponse = new NoticeDTO.Response();
        testResponse.setIsRead(false);
        testResponse.setNoticeType(NoticeType.Start);
        testResponse.setNoticeCreateDateTime(timestamp);
        testResponse.setNotId(1L);
        testResponse.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        testResponse.setNoticeMessage("test message");

        NoticeDTO.Response testResponse2 = new NoticeDTO.Response();
        testResponse2.setIsRead(true);
        testResponse2.setNoticeType(NoticeType.End);
        testResponse2.setNoticeCreateDateTime(timestamp);
        testResponse2.setNotId(2L);
        testResponse2.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440001"));
        testResponse2.setNoticeMessage("test message2");

        allResponse.add(testResponse);
        allResponse.add(testResponse2);

        //when
        given(noticeService.findAllNotice("test")).willReturn(allResponse);

        //then
        mvc.perform(get("/notifications/users/{userId}/notice", "test"))
            .andDo(print())
            .andExpect(jsonPath("$[0].cronJobId", testResponse.getCronJobId())
                .value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(jsonPath("$[1].cronJobId", testResponse2.getCronJobId())
                .value("123e4567-e89b-12d3-a456-556642440001"))
            .andExpect(jsonPath("$[0].isRead").value(false))
            .andExpect(jsonPath("$[1].isRead").value(true))
            .andExpect(status().isOk());
    }

    @Test
    public void 알림_리스트_조회_유저가_없는_경우() throws Exception {
        //given
        //when
        given(noticeService.findAllNotice("test")).willThrow(UserNotFoundException.class);

        //then
        mvc.perform(get("/notifications/users/{userId}/notice", "test"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 알림_조회() throws Exception {

        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        NoticeDTO.Response response = new NoticeDTO.Response();
        response.setIsRead(true);
        response.setNoticeType(NoticeType.Start);
        response.setNoticeCreateDateTime(timestamp);
        response.setNotId(1L);
        response.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        response.setNoticeMessage("test message");

        //when
        given(noticeService.selectNotice("test", 1L)).willReturn(response);

        //then
        mvc.perform(get("/notifications/users/{userId}/notice/{notid}", "test", 1L))
            .andDo(print())
            .andExpect(jsonPath("$.cronJobId").value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(jsonPath("$.isRead").value(true))
            .andExpect(jsonPath("$.noticeMessage").value(response.getNoticeMessage()))
            .andExpect(status().isOk());
    }

    @Test
    public void 알림_조회_유저가_없는_경우() throws Exception {

        //given

        //when
        given(noticeService.selectNotice("test", 1L)).willThrow(UserNotFoundException.class);

        //then
        mvc.perform(get("/notifications/users/{userId}/notice/{notid}", "test", 1L))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 알림_조회_알림이_없는_경우() throws Exception {

        //given

        //when
        given(noticeService.selectNotice("test", 1L)).willThrow(NoticeNotFoundException.class);

        //then
        mvc.perform(get("/notifications/users/{userId}/notice/{notid}", "test", 1L))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 알림_추가_성공() throws Exception {

        //given
        UUID uuid = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        NoticeDTO.Response response = new NoticeDTO.Response();
        response.setNoticeMessage("test");
        response.setNoticeType(NoticeType.Start);
        response.setCronJobId(uuid);
        response.setIsRead(true);
        response.setNotId(1L);
        response.setNoticeCreateDateTime(timestamp);

        //when
        NoticeDTO.Request request = new NoticeDTO.Request();
        request.setCronJobId(uuid);
        request.setNoticeType(NoticeType.Start);
        request.setNoticeMessage("test");
        request.setNoticeCreateDateTime(timestamp);

        System.out.println("time cmp: " + request.getNoticeCreateDateTime() + " " + response
            .getNoticeCreateDateTime());

        given(noticeService.createNotice(request)).willReturn(response);

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(post("/notifications/notice").content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.cronJobId").value("123e4567-e89b-12d3-a456-556642440000"))
            .andExpect(status().isOk());
    }

    @Test
    public void 알림_추가_크론잡이_없는_경우() throws Exception {

        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //when
        NoticeDTO.Request request = new NoticeDTO.Request();
        request.setNoticeMessage("test");
        request.setNoticeType(NoticeType.Start);
        request.setCronJobId(UUID.fromString("123e4567-e89b-12d3-a456-556642440000"));
        request.setNoticeCreateDateTime(timestamp);

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);
        given(noticeService.createNotice(request)).willThrow(CronJobNotFoundException.class);

        //then
        mvc.perform(post("/notifications/notice").content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}