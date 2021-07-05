package gabia.cronMonitoring.service;

import gabia.cronMonitoring.entity.RefreshToken;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(RefreshToken token) {
        return refreshTokenRepository.save(token);
    }

    public RefreshToken getRefreshToken(String userAccount) {
        RefreshToken token = refreshTokenRepository.findById(userAccount).orElseThrow(
            () -> new InvalidTokenException("해당 사용자에 대해 발급된 refresh token이 존재하지 않습니다."));
        return token;
    }

    public void deleteRefreshToken(String userAccount) {
        refreshTokenRepository.deleteById(userAccount);
    }
}
