package gabia.cronMonitoring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.CronServerDTO;
import gabia.cronMonitoring.exception.cron.server.AlreadyRegisteredServerException;
import gabia.cronMonitoring.exception.cron.server.NotExistingServerException;
import gabia.cronMonitoring.exception.cron.server.NotValidIPException;
import gabia.cronMonitoring.exception.cron.server.handler.ServerControllerExceptionHandler;
import gabia.cronMonitoring.service.CronServerService;
import java.util.ArrayList;
import java.util.List;
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
@WebMvcTest(CronServerController.class)
public class CronServerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CronServerService cronServerService;

    @InjectMocks
    private CronServerController cronServerController;

    @InjectMocks
    private ServerControllerExceptionHandler serverControllerExceptionHandler;

    @Before
    public void setup() {
        cronServerController = new CronServerController(cronServerService);
        mockMvc = standaloneSetup(cronServerController)
            .setControllerAdvice(serverControllerExceptionHandler)
            .build();
    }

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void 서버_목록_GET_성공() throws Exception {
        // Given
        List<CronServerDTO> servers = new ArrayList<>();
        servers.add(new CronServerDTO("1.1.1.1"));
        servers.add(new CronServerDTO("1.1.1.2"));
        // When
        when(cronServerService.getCronServers()).thenReturn(servers);
        // Then
        mockMvc.perform(get("/cron-servers"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].serverIp").value(servers.get(0).getServerIp()))
            .andExpect(jsonPath("$[1].serverIp").value(servers.get(1).getServerIp()))
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void 서버_등록_POST_성공() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("1.1.1.1");
        String request = mapper.writeValueAsString(cronServerDTO);
        given(cronServerService.addCronServer(any())).willReturn(cronServerDTO);
        // When
        String expectByServerIp = "$.serverIp";
        // Then
        mockMvc.perform(post("/cron-servers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath(expectByServerIp).value("1.1.1.1"));
    }

    @Test
    public void 유효하지_않은_IP로_서버_등록시_POST_예외() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("notvalidip");
        String request = mapper.writeValueAsString(cronServerDTO);
        // When
        given(cronServerService.addCronServer("notvalidip"))
            .willThrow(new NotValidIPException("유효한 IP주소가 아닙니다."));
        // Then
        mockMvc.perform(post("/cron-servers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 이미_등록된_IP로_서버_등록시_POST_예외() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("1.1.1.1");
        String request = mapper.writeValueAsString(cronServerDTO);
        // When
        given(cronServerService.addCronServer("1.1.1.1"))
            .willThrow(new AlreadyRegisteredServerException("이미 등록된 서버입니다."));
        // Then
        mockMvc.perform(post("/cron-servers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @Test
    public void 서버_수정_PATCH_성공() throws Exception {
        // Given
        String oldCronServer = "1.1.1.1";
        CronServerDTO newCronServerDto = new CronServerDTO("1.1.1.2");
        String request = mapper.writeValueAsString(newCronServerDto);
        given(cronServerService.updateCronServer(any(), any())).willReturn(newCronServerDto);
        // When
        String expectByServerIp = "$.serverIp";
        // Then
        mockMvc.perform(patch("/cron-servers/{serverIp}}", oldCronServer)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectByServerIp).value("1.1.1.2"));
    }

    @Test
    public void 유효하지_않은_IP로_서버_수정시_PATCH_예외() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("notvalidip");
        String request = mapper.writeValueAsString(cronServerDTO);
        // When
        given(cronServerService.updateCronServer(any(), any()))
            .willThrow(new NotValidIPException("입력된 새 주소가 유효한 IP주소가 아닙니다."));
        // Then
        mockMvc.perform(patch("/cron-servers/{serverIp}}", "1.1.1.1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    public void 미등록_서버_수정시_PATCH_예외() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("notvalidip");
        String request = mapper.writeValueAsString(cronServerDTO);
        // When
        given(cronServerService.updateCronServer(any(), any()))
            .willThrow(new NotExistingServerException("존재하지 않는 서버입니다."));
        // Then
        mockMvc.perform(patch("/cron-servers/{serverIp}}", "1.1.1.1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 이미_등록된_IP로_서버_수정시_PATCH_예외() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("notvalidip");
        String request = mapper.writeValueAsString(cronServerDTO);
        // When
        given(cronServerService.updateCronServer(any(), any()))
            .willThrow(new AlreadyRegisteredServerException("이미 등록된 서버입니다."));
        // Then
        mockMvc.perform(patch("/cron-servers/{serverIp}}", "1.1.1.1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @Test
    public void 서버_삭제_DELETE_성공() throws Exception {
        // Given
        // When
        // Then
        mockMvc.perform(delete("/cron-servers/{serverIp}", "1.1.1.1"))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    public void 미등록_서버_삭제시_DELETE_예외() throws Exception {
        // Given
        // When
        doThrow(new NotExistingServerException("존재하지 않는 서버입니다.")).when(cronServerService)
            .deleteCronServer(any());
        // Then
        mockMvc.perform(delete("/cron-servers/{serverIp}", "1.1.1.1"))
            .andDo(print())
            .andExpect(status().isNotFound());

    }
}