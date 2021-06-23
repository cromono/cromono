package gabia.cronMonitoring.service;

import static org.mockito.BDDMockito.given;

import gabia.cronMonitoring.dto.CronJobDTO;
import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.repository.CronJobRepository;
import gabia.cronMonitoring.repository.CronServerRepository;
import gabia.cronMonitoring.exception.cron.job.CronJobNotFoundException;
import gabia.cronMonitoring.exception.cron.job.CronServerNotFoundException;
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
    @Mock
    private CronServerRepository cronServerRepository;

    @Before
    public void init() {
        cronJobService = new CronJobService(this.cronJobRepository, this.cronServerRepository);
    }

    @Test
    @Transactional
    public void 크론_JOB_등록_성공() {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJobDTO cronJobDTO = new CronJobDTO(uuid, "* * * * * test1.sh", "test1.sh",
            new Date(), new Date(), cronServer.getIp());
        CronJob cronJob = new CronJob(uuid, "* * * * * test1.sh", "test1.sh",
            new Date(), new Date(), cronServer);

        given(cronServerRepository.findByIp(cronServer.getIp()))
            .willReturn(Optional.of(cronServer));

        //when
        CronJobDTO savedCronJobDTO = cronJobService.createCronJob(cronJobDTO);

        //then
        Assertions.assertThat(savedCronJobDTO.getId()).isEqualTo(uuid);
    }

    @Test(expected = CronServerNotFoundException.class)
    @Transactional
    public void 크론_JOB_등록_실패() {
        //given
        UUID uuid = UUID.randomUUID();
        String serverIp = "192.168.0.1";
        CronServer cronServer = createCronServer(serverIp);
        CronJobDTO cronJobDTO = new CronJobDTO(uuid, "* * * * * test1.sh", "test1.sh",
            new Date(), new Date(), serverIp);

        given(cronServerRepository.findByIp(cronServer.getIp()))
            .willReturn(Optional.empty());

        //when
        CronJobDTO savedCronJob1 = cronJobService.createCronJob(cronJobDTO);
        CronJobDTO savedCronJob3 = cronJobService.createCronJob(cronJobDTO);

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

        List<CronJobDTO> returnedJobs1 = cronJobService
            .readCronJobListByServer(cronServer1.getIp());
        List<CronJobDTO> returnedJobs2 = cronJobService
            .readCronJobListByServer(cronServer2.getIp());

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
        CronJob cronJob = new CronJob(uuid, "test1.sh", "* * * * * test1.sh",
            new Date(), new Date(), cronServer);

        String cronExpr = new String("1 1 1 1 1 test2.sh");
        String cronName = new String("test2.sh");
        CronServer cronServer2 = createCronServer("127.0.0.1");
        Date min =
            Date.from(LocalDate.of(2021, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date max =
            Date.from(LocalDate.of(2021, 7, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        CronJobDTO cronJobDTO = new CronJobDTO(uuid, cronName, cronExpr,
            min, max, cronServer2.getIp());

        given(cronJobRepository.findById(uuid)).willReturn(Optional.of(cronJob));
        given(cronServerRepository.findByIp(cronServer2.getIp()))
            .willReturn(Optional.of(cronServer2));
        //when
        CronJobDTO updatedCronJobDTO = cronJobService
            .updateCronJob(uuid, cronServer2.getIp(), cronName, cronExpr, min, max);
        //then

        Assertions.assertThat(updatedCronJobDTO).isEqualTo(cronJobDTO);
        Assertions.assertThat(updatedCronJobDTO.getCronExpr()).isEqualTo(cronExpr);
        Assertions.assertThat(updatedCronJobDTO.getCronName()).isEqualTo(cronName);
        Assertions.assertThat(updatedCronJobDTO.getServerIp())
            .isEqualTo("127.0.0.1");
        Assertions.assertThat(updatedCronJobDTO.getMaxEndTime()).isEqualTo(max);
        Assertions.assertThat(updatedCronJobDTO.getMinStartTime())
            .isEqualTo(min);

    }

    @Test(expected = CronJobNotFoundException.class)
    @Transactional
    public void 크론_잡_수정_실패() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        given(cronJobRepository.findById(uuid)).willReturn(Optional.empty());

        //when
        String cronExpr = new String("1 1 1 1 1 test2.sh");
        String cronName = new String("test2.sh");
        CronServer cronServer2 = createCronServer("127.0.0.1");
        Date date1 =
            Date.from(LocalDate.of(2021, 6, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date date2 =
            Date.from(LocalDate.of(2021, 7, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        CronJobDTO updatedCronJobDTO = cronJobService
            .updateCronJob(uuid, cronServer2.getIp(), cronName, cronExpr, date1, date2);

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
        given(cronJobRepository.findById(uuid)).willReturn(Optional.of(cronJob));
        given(
            cronJobRepository.deleteById(uuid)).willReturn(Optional.of(uuid));
        //when

        boolean ret = cronJobService.deleteCronJob(uuid);

        //then
        Assertions.assertThat(ret).isFalse();
    }

    @Test(expected = CronJobNotFoundException.class)
    @Transactional
    public void 크론_잡_삭제_실패_삭제대상없음() {
        //given
        UUID uuid = UUID.randomUUID();
        CronServer cronServer = createCronServer("192.168.0.1");
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());
        given(cronJobRepository.findById(uuid)).willReturn(Optional.empty());

        //when
        boolean ret = cronJobService.deleteCronJob(uuid);

        //then
        Assertions.fail("크론잡삭제실패 익셉션 테스트 실패, 삭제대상이 없는경우");
    }

    public CronServer createCronServer(String ip) {
        CronServer cronServer = new CronServer(ip);
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