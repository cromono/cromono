package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gabia.cronMonitoring.entity.RefreshToken;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class RefreshTokenServiceTest {

    @InjectMocks
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void 사용자_아이디로_RefreshToken_생성() throws Exception {
        // Given
        String userAccount = "test";
        RefreshToken token = RefreshToken.builder()
            .id(userAccount)
            .token(UUID.randomUUID().toString())
            .createdDate(Instant.now())
            .build();
        // When
        when(refreshTokenRepository.save(any())).thenReturn(token);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userAccount);
        // Then
        Assertions.assertThat(refreshToken).isEqualTo(token);
    }

    @Test
    void RefreshToken_유효성_검증_성공() throws Exception {
        // Given
        String token = "test";
        // When
        when(refreshTokenRepository.findByToken(token)).thenReturn(
            Optional.ofNullable(RefreshToken
                .builder()
                .id("test")
                .token(UUID.randomUUID().toString())
                .createdDate(Instant.now())
                .build()));
        // Then
        assertDoesNotThrow(() -> refreshTokenService.validateRefreshToken(token));
    }

    @Test
    void RefreshToken_유효성_검증_실패() throws Exception {
        // Given
        String token = "test";
        // When
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        // Then
        assertThrows(InvalidTokenException.class,
            () -> refreshTokenService.validateRefreshToken(token));
    }

    @Test
    void 사용자_아이디로_RefreshToken_조회() throws Exception {
        // Given
        String userAccount = "test";
        RefreshToken token = RefreshToken.builder()
            .id(userAccount)
            .token(UUID.randomUUID().toString())
            .createdDate(Instant.now())
            .build();
        // When
        when(refreshTokenRepository.findById(userAccount)).thenReturn(Optional.ofNullable(token));
        RefreshToken refreshToken = refreshTokenService
            .getRefreshTokenByUserAccount(userAccount);
        // Then
        Assertions.assertThat(refreshToken).isEqualTo(token);
    }

    @Test
    void 사용자_아이디로_RefreshToken_삭제() throws Exception {
        // Given
        String userAccount = "test";
        // When
        refreshTokenService.deleteRefreshToken(userAccount);
        when(refreshTokenRepository.findById(userAccount)).thenReturn(Optional.empty());
        // Then
        Assertions.assertThat(refreshTokenRepository.findById(userAccount)).isEmpty();
    }
}