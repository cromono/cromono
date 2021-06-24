package gabia.cronMonitoring.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import gabia.cronMonitoring.dto.UserCronJobDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.UserCronJob;
import gabia.cronMonitoring.repository.UserCronJobRepository;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserCronJobServiceTest {

    @Mock
    private UserCronJobRepository userCronJobRepository;

    @InjectMocks
    private UserCronJobService userCronJobService;

    @Before
    public void init() {
        userCronJobService = new UserCronJobService(this.userCronJobRepository);
    }

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
        cronJob.setId(UUID.randomUUID());
        cronJob.setServer(cronServer);
        cronJob.setCronExpr("test2");
        cronJob.setCronName("test2");

        UserCronJob userCronJob1 = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob)
            .build();

        UserCronJob userCronJob2 = UserCronJob.builder()
            .user(user)
            .cronJob(cronJob2)
            .build();

        List<UserCronJob> userCronJobList = new LinkedList<>();

        UserCronJob savedUserCronJob = userCronJobRepository.save(userCronJob1);
        UserCronJob savedUserCronJob2 = userCronJobRepository.save(userCronJob2);

        userCronJobList.add(savedUserCronJob);
        userCronJobList.add(savedUserCronJob2);

        given(userCronJobRepository.findByUserAccount(any()))
            .willReturn(userCronJobList);

        //when
        List<UserCronJobDTO.Response> allUserReadAuth = userCronJobService
            .findAllUserCronJob("test");

        //then
        Assertions.assertThat(allUserReadAuth.get(0).getUserId()).isEqualTo("test");
        Assertions.assertThat(allUserReadAuth.get(1).getUserId()).isEqualTo("test2");

    }

}