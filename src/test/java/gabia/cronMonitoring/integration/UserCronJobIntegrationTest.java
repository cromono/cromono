package gabia.cronMonitoring.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.controller.UserCronJobController;
import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO.Request;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.UserCronJob;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronProcessRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.repository.UserCronJobRepository;
import gabia.cronMonitoring.repository.UserRepository;
import gabia.cronMonitoring.service.CronProcessService;
import java.sql.Timestamp;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserCronJobIntegrationTest {

    private MockMvc mvc;

    @Autowired
    CronProcessService cronProcessService;

    @Autowired
    CronServerRepository cronServerRepository;

    @Autowired
    CronJobRepository cronJobRepository;

    @Autowired
    CronProcessRepository cronProcessRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCronJobRepository userCronJobRepository;

    @Autowired
    UserCronJobController userCronJobController;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUpMockMvc() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void 모든_유저크론잡_조회() throws Exception {

        //given
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        CronJob cronJob2 = new CronJob();
        cronJob2.setServer(cronServer);
        cronJob2.setCronExpr("test2");
        cronJob2.setCronName("test2");

        cronJobRepository.save(cronJob);
        cronJobRepository.save(cronJob2);

        User user = new User();
        user.setAccount("Lucas");
        user.setPassword("test");
        user.setName("jyj");
        user.setEmail("test@gabia.com");

        userRepository.save(user);

        UserCronJob userCronJob = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob)
            .build();

        UserCronJob userCronJob2 = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob2)
            .build();

        UserCronJob savedUserCronJob = userCronJobRepository.save(userCronJob);
        UserCronJob savedUserCronJob2 = userCronJobRepository.save(userCronJob2);
        //when

        //then
        mvc.perform(
            get("/cron-read-auths/users/{userId}/crons/", "Lucas"))
            .andDo(print())
            .andExpect(jsonPath("$[0].userId", savedUserCronJob.getUser().getId()).exists())
            .andExpect(jsonPath("$[1].userId", savedUserCronJob2.getUser().getId()).exists())
            .andExpect(jsonPath("$[0].cronJobId", savedUserCronJob.getCronJob().getId()).exists())
            .andExpect(jsonPath("$[1].cronJobId", savedUserCronJob2.getCronJob().getId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 유저크론잡_추가() throws Exception {

        //given
        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        User user = new User();
        user.setAccount("Lucas");
        user.setPassword("test");
        user.setName("jyj");
        user.setEmail("test@gabia.com");
        userRepository.save(user);

        UserCronJob userCronJob = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob)
            .build();

        //when
        UserCronJobDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(
            post("/cron-read-auths/users/{userId}/crons/", "Lucas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andDo(print())
            .andExpect(jsonPath("$.userId", user.getId()).exists())
            .andExpect(jsonPath("$.cronJobId", cronJob.getId()).exists())
            .andExpect(status().isOk());
    }

    @Test
    public void 유저크론잡_삭제() throws Exception {

        //given
        CronServer cronServer = new CronServer("0.0.0.0");
        cronServerRepository.save(cronServer);

        CronJob cronJob = new CronJob();
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");
        cronJobRepository.save(cronJob);

        User user = new User();
        user.setAccount("Lucas");
        user.setPassword("test");
        user.setName("jyj");
        user.setEmail("test@gabia.com");
        userRepository.save(user);

        UserCronJob userCronJob = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob)
            .build();
        userCronJobRepository.save(userCronJob);

        //when
        UserCronJobDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());

        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(request);

        //then
        mvc.perform(
            post("/cron-read-auths/users/{userId}/crons/", "Lucas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andDo(print())
            .andExpect(jsonPath("$.userId", user.getId()).exists())
            .andExpect(jsonPath("$.cronJobId", cronJob.getId()).exists())
            .andExpect(status().isOk());
    }


}