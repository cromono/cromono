package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.CronServerDTO;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.exception.cron.server.AlreadyRegisteredServerException;
import gabia.cronMonitoring.exception.cron.server.NotExistingServerException;
import gabia.cronMonitoring.exception.cron.server.NotValidIPException;
import gabia.cronMonitoring.repositoryImpl.CronServerRepositoryImpl;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CronServerService {

    private final CronServerRepositoryImpl cronServerRepository;

    public List<CronServerDTO> getCronServers() {
        return cronServerRepository.findAll().stream().map(s -> CronServerDTO.from(s))
            .collect(
                Collectors.toList());
    }

    public CronServerDTO getCronServer(String ip) {
        CronServer cronServer = cronServerRepository.findByIp(ip).get();
        return CronServerDTO.from(cronServer);
    }

    @Transactional
    public CronServerDTO addCronServer(String ip) {

        if (!isValidIp(ip)) {
            throw new NotValidIPException("유효한 IP주소가 아닙니다.");
        }
        Optional<CronServer> findServer = cronServerRepository.findByIp(ip);
        if (!findServer.isEmpty()) {
            throw new AlreadyRegisteredServerException("이미 등록된 서버입니다.");
        }
        CronServer cronServer = new CronServer(ip);
        CronServer save = cronServerRepository.save(cronServer);
        return CronServerDTO.from(save);
    }

    @Transactional
    public CronServerDTO updateCronServer(String oldIp, String newIp) {
        if (!isValidIp(oldIp)) {
            throw new NotValidIPException("유효한 IP주소가 아닙니다.");
        }
        if (!isValidIp(newIp)) {
            throw new NotValidIPException("유효한 IP주소가 아닙니다.");
        }

        Optional<CronServer> existingServer = cronServerRepository.findByIp(oldIp);
        Optional<CronServer> newServer = cronServerRepository.findByIp(newIp);
        if (existingServer.isEmpty()) {
            throw new NotExistingServerException("존재하지 않는 서버입니다.");
        }
        if (!newServer.isEmpty()) {
            throw new AlreadyRegisteredServerException("이미 등록된 서버입니다.");
        }
        CronServer server = existingServer.get();
        server.setIp(newIp);
        return CronServerDTO.from(server);
    }

    @Transactional
    public void deleteCronServer(String ip) {
        if (!isValidIp(ip)) {
            throw new NotValidIPException("유효한 IP주소가 아닙니다.");
        }
        Optional<CronServer> findServer = cronServerRepository.findByIp(ip);
        if (findServer.isEmpty()) {
            throw new NotExistingServerException("존재하지 않는 서버입니다.");
        }
        CronServer cronServer = findServer.get();
        cronServerRepository.delete(cronServer);
    }

    //TODO : 논의 후 외부 라이브러리로 대체
    //참고자료 : https://stackoverflow.com/questions/5667371/validate-ipv4-address-in-java/5668971#5668971
    //http://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/InetAddressValidator.html
    public boolean isValidIp(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }
            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
