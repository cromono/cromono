package gabia.cronMonitoring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.exception.cron.server.AlreadyRegisteredServerException;
import gabia.cronMonitoring.exception.cron.server.NotExistingServerException;
import gabia.cronMonitoring.exception.cron.server.NotValidIPException;
import gabia.cronMonitoring.repositoryImpl.CronServerRepositoryImpl;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class CronServerServiceTest {

    @InjectMocks
    CronServerService cronServerService;
    @Mock
    CronServerRepositoryImpl cronServerRepository;

    @Test
    public void 서버등록() throws Exception {
        // Given
        String ip = "1.1.1.1";
        CronServer mock = new CronServer(ip);
        given(cronServerRepository.save(any())).willReturn(mock);
        // When
        cronServerService.addCronServer(ip);
        given(cronServerRepository.findByIp(ip)).willReturn(java.util.Optional.of(mock));
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
        given(cronServerRepository.save(any())).willReturn(mock);
        // When
        cronServerService.addCronServer(ip);
        given(cronServerRepository.findByIp(ip)).willReturn(java.util.Optional.of(mock));
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
        given(cronServerRepository.save(any())).willReturn(mock);
        // When
        cronServerService.addCronServer(oldIp);
        given(cronServerRepository.findByIp(oldIp)).willReturn(Optional.of(mock));
        given(cronServerRepository.findByIp(newIp)).willReturn(Optional.empty());
        CronServer serverBeforeUpdate = cronServerRepository.findByIp(oldIp).get();
        cronServerService.updateCronServer(oldIp, newIp);
        given(cronServerRepository.findByIp(newIp)).willReturn(Optional.of(mock));
        CronServer serverAfterUpdate = cronServerRepository.findByIp(newIp).get();
        // Then
        assertEquals("수정 전후로 가져온 엔티티가 동일해야 한다.", serverBeforeUpdate, serverAfterUpdate);
    }

    @Test(expected = NotValidIPException.class)
    public void 유효하지_않은_기존_서버_수정시_예외() throws Exception {
        // Given
        String ip = "invalidip";
        // When
        cronServerService.updateCronServer(ip, "1.1.1.2");
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }

    @Test(expected = NotValidIPException.class)
    public void 유효하지_않은_새_서버로_수정시_예외() throws Exception {
        // Given
        String oldIp = "1.1.1.1";
        String newIp = "invalidip";
        CronServer mock = new CronServer(oldIp);
        // When
        cronServerService.updateCronServer(oldIp, newIp);
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }

    @Test(expected = NotExistingServerException.class)
    public void 미등록_서버_수정시_예외() throws Exception {
        // Given
        String ip = "1.1.1.1";
        given(cronServerRepository.findByIp(ip)).willReturn(Optional.empty());
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
        CronServer mock = new CronServer(registeredIp);
        given(cronServerRepository.findByIp(registeredIp)).willReturn(Optional.of(mock));
        given(cronServerRepository.findByIp(randomIp)).willReturn(Optional.of(mock));
        // When
        cronServerService.updateCronServer(randomIp, registeredIp);
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }

    @Test
    public void 서버삭제() throws Exception {
        // Given
        String ip = "1.1.1.1";
        CronServer mock = new CronServer(ip);
        given(cronServerRepository.save(any())).willReturn(mock);
        cronServerService.addCronServer(ip);
        // When
        given(cronServerRepository.findByIp(ip)).willReturn(Optional.of(mock));
        cronServerService.deleteCronServer(ip);
        given(cronServerRepository.findByIp(ip)).willReturn(Optional.empty());
        // Then
        assertThat(cronServerRepository.findByIp(ip)).isEmpty();
    }

    @Test(expected = NotExistingServerException.class)
    public void 미등록_서버_삭제시_예외() throws Exception {
        // Given
        String ip = "1.1.1.1";
        CronServer mock = new CronServer(ip);
        given(cronServerRepository.save(any())).willReturn(mock);
        cronServerService.addCronServer(ip);
        given(cronServerRepository.findByIp(ip)).willReturn(Optional.of(mock));
        cronServerService.deleteCronServer(ip);
        given(cronServerRepository.findByIp(ip)).willReturn(Optional.empty());
        // When
        cronServerService.deleteCronServer(ip);
        // Then
        Assert.fail("예외가 발생해야 한다.");
    }
}