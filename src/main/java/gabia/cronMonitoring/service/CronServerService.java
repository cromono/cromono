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
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CronServerService {

    private final CronServerRepositoryImpl cronServerRepository;

    private InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

    public List<CronServerDTO> getCronServers() {
        return cronServerRepository.findAll().stream()
            .map(s -> CronServerDTO.from(s))
            .collect(Collectors.toList());
    }
    
    // 추후 필요시 주석해제
//    public CronServerDTO getCronServer(String ip) {
//        CronServer cronServer = cronServerRepository.findByIp(ip).get();
//        return CronServerDTO.from(cronServer);
//    }

    @Transactional
    public CronServerDTO addCronServer(String ip) {

        if (!inetAddressValidator.isValid(ip)) {
            throw new NotValidIPException("유효한 IP주소가 아닙니다.");
        }
        this.cronServerRepository.findByIp(ip).ifPresent(none -> {
            throw new AlreadyRegisteredServerException("이미 등록된 서버입니다.");
        });
        CronServer cronServer = new CronServer(ip);
        CronServer save = cronServerRepository.save(cronServer);
        return CronServerDTO.from(save);
    }

    @Transactional
    public CronServerDTO updateCronServer(String oldIp, String newIp) {
        if (!inetAddressValidator.isValid(oldIp)) {
            throw new NotValidIPException("입력된 이전 주소가 유효한 IP주소가 아닙니다.");
        }
        if (!inetAddressValidator.isValid(newIp)) {
            throw new NotValidIPException("입력된 새 주소가 유효한 IP주소가 아닙니다.");
        }

        Optional<CronServer> existingServer = cronServerRepository.findByIp(oldIp);
        Optional<CronServer> newServer = cronServerRepository.findByIp(newIp);
        existingServer.orElseThrow(() -> new NotExistingServerException("존재하지 않는 서버입니다."));
        newServer.ifPresent(none -> {
            throw new AlreadyRegisteredServerException("이미 등록된 서버입니다.");
        });

        CronServer server = existingServer.get();
        server.setIp(newIp);
        return CronServerDTO.from(server);
    }

    @Transactional
    public void deleteCronServer(String ip) {
        if (!inetAddressValidator.isValid(ip)) {
            throw new NotValidIPException("유효한 IP주소가 아닙니다.");
        }
        Optional<CronServer> findServer = cronServerRepository.findByIp(ip);
        findServer.orElseThrow(() -> new NotExistingServerException("존재하지 않는 서버입니다."));
        CronServer cronServer = findServer.get();
        cronServerRepository.delete(cronServer);
    }
}
