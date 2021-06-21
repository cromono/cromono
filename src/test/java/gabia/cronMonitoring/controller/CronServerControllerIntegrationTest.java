package gabia.cronMonitoring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.CronServerDTO;
import gabia.cronMonitoring.service.CronServerService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class CronServerControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private CronServerController cronServerController;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void 서버_목록_GET() throws Exception {
        // Given
        List<CronServerDTO> servers = new ArrayList<>();
        servers.add(new CronServerDTO("1.1.1.1"));
        servers.add(new CronServerDTO("1.1.1.2"));
        cronServerController.postCronServers(new CronServerDTO("1.1.1.1"));
        cronServerController.postCronServers(new CronServerDTO("1.1.1.2"));
        // When
        String expectAllServerIp = "$.[*]";
        // Then
        mockMvc.perform(get("/cron-servers"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectAllServerIp, servers).exists())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void 서버_등록_POST() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("1.1.1.1");
        String request = mapper.writeValueAsString(cronServerDTO);
        // When
        String expectByServerIp = "$.serverIp";
        // Then
        mockMvc.perform(post("/cron-servers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath(expectByServerIp, "1.1.1.1").exists());
    }

    @Test
    public void 서버_수정_PATCH() throws Exception {
        // Given
        CronServerDTO oldCronServerDto = new CronServerDTO("1.1.1.1");
        CronServerDTO newCronServerDto = new CronServerDTO("1.1.1.2");
        String request = mapper.writeValueAsString(newCronServerDto);
        cronServerController.postCronServers(oldCronServerDto);
        // When
        String expectByServerIp = "$.serverIp";
        // Then
        mockMvc.perform(patch("/cron-servers/{serverIp}", "1.1.1.1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectByServerIp, "1.1.1.2").exists());
    }

    @Test
    public void 서버_삭제_DELETE() throws Exception {
        // Given
        CronServerDTO cronServerDTO = new CronServerDTO("1.1.1.1");
        String request = mapper.writeValueAsString(cronServerDTO);
        cronServerController.postCronServers(cronServerDTO);
        // When
        String expectByServerIp = "$.serverIp";
        // Then
        mockMvc.perform(delete("/cron-servers/{serverIp}", "1.1.1.1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andDo(print())
            .andExpect(status().isNoContent());
    }
}