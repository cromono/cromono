package gabia.cronMonitoring.service;

import static org.mockito.BDDMockito.given;

import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.service.exception.CronJobExistedException;
import gabia.cronMonitoring.service.exception.CronJobNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(MockitoJUnitRunner.class)
public class CronJobServiceTest {

    private CronJobService cronJobService;

    @Mock
    private CronJobRepository cronJobRepository;

    @Before
    public void init() {
        cronJobService = new CronJobService(this.cronJobRepository);
    }

    @Test
    @Transactional
    public void 크론_JOB_등록_성공() {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        //given(cronJobRepository.findOne(uuid)).willReturn(Optional.of(cronJob));
        given(cronJobRepository.findOne(uuid)).willReturn(Optional.empty());
        given(cronJobRepository.save(cronJob)).willReturn(Optional.of(uuid));

        //when
        UUID savedCronJobId = cronJobService.createCronJob(cronJob);

        //then
        Assertions.assertThat(savedCronJobId).isEqualTo(uuid);
        Assertions.assertThat(savedCronJobId).isEqualTo(cronJob.getId());
    }

    @Test(expected = CronJobExistedException.class)
    @Transactional
    public void 크론_JOB_등록_실패() {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob1 = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        CronJob cronJob2 = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        given(cronJobRepository.findOne(uuid)).willReturn(Optional.of(cronJob1));

        //when
        UUID savedCronJobId1 = cronJobService.createCronJob(cronJob1);
        UUID savedCronJobId3 = cronJobService.createCronJob(cronJob2);
        UUID savedCronJobId2 = cronJobService.createCronJob(cronJob1);

        //then
        Assertions.fail("테스트 실패");
    }

    @Test
    @Transactional
    public void 크론_JOB_서버로_조회_성공() throws Exception {
        //given
        CronServer cronServer1 = createCronServer("192.168.0.1");
        CronServer cronServer2 = createCronServer("192.168.0.2");
        List<CronJob> cronJobsTotal = new LinkedList<>();
        List<CronJob> cronJobs1 = new LinkedList<>();
        List<CronJob> cronJobs2 = new LinkedList<>();
        for (int i = 0; i < 6; i++) {
            CronServer cronServer = i % 2 == 0 ? cronServer1 : cronServer2;
            List<CronJob> cronJobs = i % 2 == 0 ? cronJobs1 : cronJobs2;
            CronJob cronJob = createCronJob(UUID.randomUUID(), "* * * * * test1.sh", "test1.sh",
                cronServer,
                new Date(), new Date());
            cronJobs.add(cronJob);
        }
        cronJobsTotal.addAll(cronJobs1);
        cronJobsTotal.addAll(cronJobs2);
        given(cronJobRepository.findByServer(cronServer1.getIp())).willReturn(cronJobs1);
        given(cronJobRepository.findByServer(cronServer2.getIp())).willReturn(cronJobs2);

        //when

        List<CronJob> returnedJobs1 = cronJobService.readCronJobListByServer(cronServer1.getIp());
        List<CronJob> returnedJobs2 = cronJobService.readCronJobListByServer(cronServer2.getIp());

        //then
        Assertions.assertThat(returnedJobs1.size()).isEqualTo(3);
        Assertions.assertThat(returnedJobs2.size()).isEqualTo(3);
        for (int i = 0; i < 3; i++) {
            Assertions.assertThat(cronJobs1.get(i).getServer().getIp()).isEqualTo("192.168.0.1");
            Assertions.assertThat(cronJobs1.get(i).getServer().getIp()).isNotEqualTo("192.168.0.2");
            Assertions.assertThat(cronJobs2.get(i).getServer().getIp()).isEqualTo("192.168.0.2");
            Assertions.assertThat(cronJobs2.get(i).getServer().getIp()).isNotEqualTo("192.168.0.1");
        }
    }

    @Test
    @Transactional
    //@Rollback(false)
    public void 크론_잡_수정_성공() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        given(cronJobRepository.findOne(uuid)).willReturn(Optional.of(cronJob));

        //when
        String cronExpr = new String("1 1 1 1 1 test2.sh");
        String cronName = new String("test2.sh");
        CronServer cronServer2 = createCronServer("127.0.0.1");
        Date date1 =
            Date.from(LocalDate.of(2021, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date date2 =
            Date.from(LocalDate.of(2021, 7, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        CronJob updatedCronJob = cronJobService
            .updateCronJob(uuid, cronServer2, cronName, cronExpr, date1, date2);
        //then

        Assertions.assertThat(updatedCronJob).isEqualTo(cronJob);
        Assertions.assertThat(updatedCronJob.getCronExpr()).isEqualTo(cronJob.getCronExpr());
        Assertions.assertThat(updatedCronJob.getCronName()).isEqualTo(cronJob.getCronName());
        Assertions.assertThat(updatedCronJob.getServer().getIp())
            .isEqualTo(cronJob.getServer().getIp());
        Assertions.assertThat(updatedCronJob.getMaxEndTime()).isEqualTo(cronJob.getMaxEndTime());
        Assertions.assertThat(updatedCronJob.getMinStartTime())
            .isEqualTo(cronJob.getMinStartTime());

    }

    @Test(expected = CronJobNotFoundException.class)
    @Transactional
    public void 크론_잡_수정_실패() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        given(cronJobRepository.findOne(uuid)).willReturn(Optional.empty());

        //when
        String cronExpr = new String("1 1 1 1 1 test2.sh");
        String cronName = new String("test2.sh");
        CronServer cronServer2 = createCronServer("127.0.0.1");
        Date date1 =
            Date.from(LocalDate.of(2021, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date date2 =
            Date.from(LocalDate.of(2021, 7, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        CronJob updatedCronJob = cronJobService
            .updateCronJob(uuid, cronServer2, cronName, cronExpr, date1, date2);

        //then
        Assertions.fail("수정 대상이 없어서 실패");
    }

    @Test
    @Transactional
    public void 크론_잡_삭제_성공() {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        given(cronJobRepository.findOne(uuid)).willReturn(Optional.of(cronJob));
        given(
            cronJobRepository.deleteById(uuid)).willReturn(Optional.of(uuid));
        //when

        boolean ret = cronJobService.deleteCronJob(uuid);

        //then
        Assertions.assertThat(ret).isTrue();
    }

    @Test(expected = CronJobNotFoundException.class)
    @Transactional
    public void 크론_잡_삭제_실패_삭제대상없음() {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        given(cronJobRepository.findOne(uuid)).willReturn(Optional.empty());

        //when
        boolean ret = cronJobService.deleteCronJob(uuid);

        //then
        Assertions.fail("크론잡삭제실패 익셉션 테스트 실패, 삭제대상이 없는경우");
    }

    public CronServer createCronServer(String ip) {
        CronServer cronServer = new CronServer();
        cronServer.setIp(ip);
        return cronServer;
    }

    public CronJob createCronJob(UUID uuid, String expr, String name, CronServer cronServer,
        Date maxEndTime, Date minStartTime) {

        CronJob cronJob = new CronJob();
        cronJob.setId(uuid);
        cronJob.setCronExpr("* * * * * test1.sh");
        cronJob.setCronName("test1.sh");
        cronJob.setServer(cronServer);
        cronJob.setMaxEndTime(new Date());
        cronJob.setMinStartTime(new Date());

        return cronJob;
    }
}