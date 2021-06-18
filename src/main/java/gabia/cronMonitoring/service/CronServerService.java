package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.CronServerDTO;
import gabia.cronMonitoring.entity.CronServer;
import gabia.cronMonitoring.exception.cron.server.AlreadyRegisteredServerException;
import gabia.cronMonitoring.exception.cron.server.NotExistingServerException;
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

    public CronServer getCronServer(String ip) {
        return cronServerRepository.findByIp(ip).get();
    }

    @Transactional
    public CronServerDTO addCronServer(String ip) {
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
        Optional<CronServer> findServer = cronServerRepository.findByIp(ip);
        if (findServer.isEmpty()) {
            throw new NotExistingServerException("존재하지 않는 서버입니다.");
        }
        CronServer cronServer = findServer.get();
        cronServerRepository.delete(cronServer);
    }
}
