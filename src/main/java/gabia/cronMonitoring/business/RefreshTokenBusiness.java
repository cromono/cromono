package gabia.cronMonitoring.business;

import gabia.cronMonitoring.entity.RefreshToken;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenBusiness {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 리프레시 토큰 저장
     * @param token 저장할 리프레시 토큰 객체
     * @return 저장된 리프레시 토큰 객체
     */
    public RefreshToken saveRefreshToken(RefreshToken token) {
        return refreshTokenRepository.save(token);
    }

    /**
     * 특정 사용자의 리프레시 토큰 조회
     * @param userAccount 사용자 ID
     * @return 리프레시 토큰 객체
     */
    public RefreshToken getRefreshToken(String userAccount) {
        RefreshToken token = refreshTokenRepository.findById(userAccount).orElseThrow(
            () -> new InvalidTokenException("해당 사용자에 대해 발급된 refresh token이 존재하지 않습니다."));
        return token;
    }

    /**
     * 리프레시 토큰 삭제
     * @param userAccount 사용자 ID
     */
    public void deleteRefreshToken(String userAccount) {
        refreshTokenRepository.deleteById(userAccount);
    }
}
