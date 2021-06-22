package gabia.cronMonitoring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.exception.cron.server.AlreadyRegisteredServerException;
import gabia.cronMonitoring.exception.cron.server.NotExistingServerException;
import gabia.cronMonitoring.exception.cron.server.NotValidIPException;
import gabia.cronMonitoring.repositoryImpl.CronServerRepositoryImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CronServerServiceIntegrationTest {

    @Autowired
    CronServerService cronServerService;

    @Autowired
    CronServerRepositoryImpl cronServerRepository;

    @Test
    public void 서버등록() throws Exception {
        // Given
        String ip = "1.1.1.1";
        // When
        cronServerService.addCronServer(ip);
        // Then
        CronServer cronServer = cronServerRepository.findByIp(ip).get();
        assertEquals("정확한 IP주소가 등록돼야 한다.", cronServer.getIp(), ip);
    }

    @Test(expected = NotValidIPException.class)
    public void 무효한_IP_등록시_예외() throws Exception {
        // Given
        String ip = "1";
        // When
        cronServerService.addCronServer(ip);
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }

    @Test(expected = AlreadyRegisteredServerException.class)
    public void 이미_등록된_서버_등록시_예외() throws Exception {
        // Given
        String ip = "1.1.1.1";
        CronServer mock = new CronServer(ip);
        // When
        cronServerService.addCronServer(ip);
        cronServerService.addCronServer(ip);
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }

    @Test
    public void 서버수정() throws Exception {
        // Given
        String oldIp = "1.1.1.1";
        String newIp = "1.1.1.2";
        CronServer mock = new CronServer(oldIp);
        // When
        cronServerService.addCronServer(oldIp);
        CronServer serverBeforeUpdate = cronServerRepository.findByIp(oldIp).get();
        cronServerService.updateCronServer(oldIp, newIp);
        CronServer serverAfterUpdate = cronServerRepository.findByIp(newIp).get();
        // Then
        assertEquals("수정 전후로 가져온 엔티티가 동일해야 한다.", serverBeforeUpdate, serverAfterUpdate);
    }

    @Test(expected = NotExistingServerException.class)
    public void 미등록_서버_수정시_예외() throws Exception {
        // Given
        String ip = "1.1.1.1";
        // When
        cronServerService.updateCronServer(ip, "1.1.1.2");
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }

    @Test(expected = AlreadyRegisteredServerException.class)
    public void 이미_등록된_서버로_수정시_예외() throws Exception {
        // Given
        String registeredIp = "1.1.1.1";
        String randomIp = "1.1.1.2";
        cronServerService.addCronServer(registeredIp);
        cronServerService.addCronServer(randomIp);
        // When
        cronServerService.updateCronServer(randomIp, registeredIp);
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }

    @Test
    public void 서버삭제() throws Exception {
        // Given
        String ip = "1.1.1.1";
        cronServerService.addCronServer(ip);
        // When
        cronServerService.deleteCronServer(ip);
        // Then
        assertThat(cronServerRepository.findByIp(ip)).isEmpty();
    }

    @Test(expected = NotExistingServerException.class)
    public void 미등록_서버_삭제시_예외() throws Exception {
        // Given
        String ip = "1.1.1.1";
        CronServer mock = new CronServer(ip);
        cronServerService.addCronServer(ip);
        cronServerService.deleteCronServer(ip);
        // When
        cronServerService.deleteCronServer(ip);
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }
}