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

/**
 * 크론 서버와 관련된 서비스를 처리하는 클래스입니다.
 *
 * @author : 김기정(Luke)
 **/
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CronServerService {

    private final CronServerRepositoryImpl cronServerRepository;

    private InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

    /**
     * 저장된 모든 크론 서버 조회
     *
     * @return
     */
    public List<CronServerDTO> getCronServers() {
        return cronServerRepository.findAll().stream()
            .map(s -> CronServerDTO.from(s))
            .collect(Collectors.toList());
    }

    // 추후 필요시 주석해제
//    /**
//     * 특정 서버 조회
//     * @param ip ip 주소
//     * @return 서버 객체
//     */
//    public CronServerDTO getCronServer(String ip) {
//        CronServer cronServer = cronServerRepository.findByIp(ip).get();
//        return CronServerDTO.from(cronServer);
//    }

    /**
     * 크론 서버 추가
     *
     * @param ip 추가할 서버의 IP 주소
     * @return 저장된 서버에 대한 크론 서버 DTO
     */
    @Transactional
    public CronServerDTO addCronServer(String ip) {

        // IP 주소 유효성 검증
        if (!inetAddressValidator.isValid(ip)) {
            throw new NotValidIPException("유효한 IP주소가 아닙니다.");
        }
        this.cronServerRepository.findByIp(ip).ifPresent(none -> {
            throw new AlreadyRegisteredServerException("이미 등록된 서버입니다.");
        });

        // 객체 저장
        CronServer cronServer = new CronServer(ip);
        CronServer save = cronServerRepository.save(cronServer);
        return CronServerDTO.from(save);
    }

    /**
     * 크론 서버 정보 갱신
     *
     * @param oldIp 이전 IP 주소
     * @param newIp 새로 변경할 IP 주소
     * @return 갱신된 서버에 대한 서버 DTO
     */
    @Transactional
    public CronServerDTO updateCronServer(String oldIp, String newIp) {

        // IP 주소 유효성 검증
        if (!inetAddressValidator.isValid(oldIp)) {
            throw new NotValidIPException("입력된 이전 주소가 유효한 IP주소가 아닙니다.");
        }
        if (!inetAddressValidator.isValid(newIp)) {
            throw new NotValidIPException("입력된 새 주소가 유효한 IP주소가 아닙니다.");
        }

        // 객체 정보 갱신
        CronServer existingServer = cronServerRepository.findByIp(oldIp)
            .orElseThrow(() -> new NotExistingServerException("존재하지 않는 서버입니다."));
        cronServerRepository.findByIp(newIp).ifPresent(none -> {
            throw new AlreadyRegisteredServerException("이미 등록된 서버입니다.");
        });
        existingServer.setIp(newIp);
        return CronServerDTO.from(existingServer);
    }

    /**
     * 크론 서버 삭제
     *
     * @param ip 삭제할 서버 IP 주소
     */
    @Transactional
    public void deleteCronServer(String ip) {

        // 서버 삭제
        CronServer findServer = cronServerRepository.findByIp(ip).orElseThrow(() -> new NotExistingServerException("존재하지 않는 서버입니다."));
        cronServerRepository.delete(findServer);
    }
}
