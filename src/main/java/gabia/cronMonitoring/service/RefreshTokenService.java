package gabia.cronMonitoring.service;

import gabia.cronMonitoring.entity.RefreshToken;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken(String userAccount) {
        RefreshToken refreshToken = RefreshToken.builder()
            .id(userAccount)
            .token(UUID.randomUUID().toString())
            .createdDate(Instant.now())
            .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).orElseThrow(() -> new InvalidTokenException("Invalid Refresh Token"));
    }

    public RefreshToken getRefreshTokenByUserAccount(String userAccount) {
        RefreshToken token = refreshTokenRepository.findById(userAccount).orElseThrow(() -> new InvalidTokenException("해당 사용자 계정으로 발급된 refresh token이 존재하지 않습니다."));
        return token;
    }

    public void deleteRefreshToken(String userAccount) {
        refreshTokenRepository.deleteById(userAccount);
    }
}
