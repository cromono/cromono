package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.dto.UserCronJobDTO.Request;
import gabia.cronMonitoring.dto.UserCronJobDTO.Response;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.UserCronJob;
import gabia.cronMonitoring.exception.cron.process.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.user.UserNotFoundException;
import gabia.cronMonitoring.exception.usercronjob.AlreadyExistUserCronJobException;
import gabia.cronMonitoring.repository.CronJobRepositoryDataJpa;
import gabia.cronMonitoring.repository.UserCronJobRepository;
import gabia.cronMonitoring.repository.UserRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@RunWith(MockitoJUnitRunner.class)
@AutoConfigureMockMvc
class UserCronJobServiceTest {

    @Mock
    private UserCronJobRepository userCronJobRepository;

    @Mock
    private CronJobRepositoryDataJpa cronJobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCronJobService userCronJobService;

    @Test
    public void findAllUserCronJob() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        User user = new User();
        user.setId(1L);
        user.setAccount("test");
        user.setEmail("test");
        user.setName("test");
        user.setPassword("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        CronJob cronJob2 = new CronJob();
        cronJob2.setId(UUID.randomUUID());
        cronJob2.setServer(cronServer);
        cronJob2.setCronExpr("test2");
        cronJob2.setCronName("test2");

        UserCronJob userCronJob1 = UserCronJob.builder()
            .id(1L)
            .user(user)
            .cronJob(cronJob)
            .build();

        UserCronJob userCronJob2 = UserCronJob.builder()
            .id(2L)
            .user(user)
            .cronJob(cronJob2)
            .build();

        List<UserCronJob> userCronJobList = new LinkedList<>();

        userCronJobList.add(userCronJob1);
        userCronJobList.add(userCronJob2);

        given(userCronJobRepository.findByUserAccount("test"))
            .willReturn(userCronJobList);

        //when
        List<UserCronJobDTO.Response> allUserReadAuth = userCronJobService
            .findAllUserCronJob("test");

        //then
        Assertions.assertThat(allUserReadAuth.get(0).getUserAccount()).isEqualTo("test");
        Assertions.assertThat(allUserReadAuth.get(0).getCronJobId()).isEqualTo(cronJob.getId());
        Assertions.assertThat(allUserReadAuth.get(1).getUserAccount()).isEqualTo("test");
        Assertions.assertThat(allUserReadAuth.get(1).getCronJobId()).isEqualTo(cronJob2.getId());
    }

    @Test
    void addUserCronJob_유저_크론잡_모두_존재하는_경우() throws JsonProcessingException {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        User user = new User();
        user.setId(1L);
        user.setAccount("test");
        user.setEmail("test");
        user.setName("test");
        user.setPassword("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        UserCronJob userCronJob1 = UserCronJob.builder()
            .id(1L)
            .user(user)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount("test")).willReturn(Optional.of(user));

        given(userCronJobRepository.save(any(UserCronJob.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        UserCronJobDTO.Request request = new Request();
        request.setCronJobId(cronJob.getId());

        Response response = userCronJobService.addUserCronJob("test", request);

        //then
        Assertions.assertThat(response.getUserAccount()).isEqualTo("test");
        Assertions.assertThat(response.getCronJobId()).isEqualTo(cronJob.getId());

    }

    @Test
    void addUserCronJob_유저가_없는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        User user = new User();
        user.setId(1L);
        user.setAccount("test");
        user.setEmail("test");
        user.setName("test");
        user.setPassword("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        UserCronJob userCronJob1 = UserCronJob.builder()
            .id(1L)
            .user(user)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount("test")).willReturn(Optional.empty());

        given(userCronJobRepository.save(any(UserCronJob.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        assertThrows(UserNotFoundException.class, () -> {
            UserCronJobDTO.Request request = new Request();
            request.setCronJobId(cronJob.getId());
            Response response = userCronJobService.addUserCronJob("test", request);
        });

    }

    @Test
    void addUserCronJob_크론잡이_없는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        User user = new User();
        user.setId(1L);
        user.setAccount("test");
        user.setEmail("test");
        user.setName("test");
        user.setPassword("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        UserCronJob userCronJob1 = UserCronJob.builder()
            .id(1L)
            .user(user)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
        given(userRepository.findByAccount("test")).willReturn(Optional.of(user));

        given(userCronJobRepository.save(any(UserCronJob.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        assertThrows(CronJobNotFoundException.class, () -> {
            UserCronJobDTO.Request request = new Request();
            request.setCronJobId(cronJob.getId());
            Response response = userCronJobService.addUserCronJob("test", request);
        });

    }

    @Test
    void addUserCronJob_유저_크론잡이_이미_존재하는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        User user = new User();
        user.setId(1L);
        user.setAccount("test");
        user.setEmail("test");
        user.setName("test");
        user.setPassword("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        UserCronJob userCronJob1 = UserCronJob.builder()
            .id(1L)
            .user(user)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount("test")).willReturn(Optional.of(user));
        given(userCronJobRepository.findByUserAccountAndCronJobId("test", cronJob.getId()))
            .willReturn(Optional.of(userCronJob1));

        given(userCronJobRepository.save(any(UserCronJob.class)))
            .willAnswer(AdditionalAnswers.returnsFirstArg());

        //when
        assertThrows(AlreadyExistUserCronJobException.class, () -> {
            UserCronJobDTO.Request request = new Request();
            request.setCronJobId(cronJob.getId());
            Response response = userCronJobService.addUserCronJob("test", request);
        });

    }

    @Test
    void removeUserCronJob_크론잡_유저가_존재하는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        User user = new User();
        user.setId(1L);
        user.setAccount("test");
        user.setEmail("test");
        user.setName("test");
        user.setPassword("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        UserCronJob userCronJob1 = UserCronJob.builder()
            .id(1L)
            .user(user)
            .cronJob(cronJob)
            .build();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount("test")).willReturn(Optional.of(user));

        userCronJobService.removeUserCronJob(user.getAccount(), cronJob.getId());

        assertThrows(CronJobNotFoundException.class, () -> {
            given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
            given(userRepository.findByAccount("test")).willReturn(Optional.empty());

            userCronJobService.removeUserCronJob(user.getAccount(), cronJob.getId());
        });

    }

    @Test
    void removeUserCronJob_유저가_존재하지_않는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        List<UserCronJob> userCronJobList = new LinkedList<>();

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.of(cronJob));
        given(userRepository.findByAccount("test")).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userCronJobService.removeUserCronJob("test", cronJob.getId());
        });

    }

    @Test
    void removeUserCronJob_크론잡이_존재하지_않는_경우() {
        //given
        openMocks(this);

        CronServer cronServer = new CronServer("0.0.0.0");

        User user = new User();
        user.setId(1L);
        user.setAccount("test");
        user.setEmail("test");
        user.setName("test");
        user.setPassword("test");

        CronJob cronJob = new CronJob();
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test");
        cronJob.setCronName("test");

        given(cronJobRepository.findById(cronJob.getId())).willReturn(Optional.empty());
        given(userRepository.findByAccount("test")).willReturn(Optional.of(user));

        assertThrows(CronJobNotFoundException.class, () -> {
            userCronJobService.removeUserCronJob(user.getAccount(), cronJob.getId());
        });
    }
}