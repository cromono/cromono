package gabia.cronMonitoring.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.controller.CronServerController;
import gabia.cronMonitoring.dto.CronServerDTO;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active:common")
public class CronServerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private CronServerController cronServerController;

    @Autowired
    ObjectMapper mapper;

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
        // Then
        mockMvc.perform(get("/cron-servers"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].serverIp").value(servers.get(0).getServerIp()))
            .andExpect(jsonPath("$[1].serverIp").value(servers.get(1).getServerIp()))
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
            .andExpect(jsonPath(expectByServerIp).value("1.1.1.1"));
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
            .andExpect(jsonPath(expectByServerIp).value("1.1.1.2"));
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