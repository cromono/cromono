package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gabia.cronMonitoring.entity.RefreshToken;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
import java.util.Optional;
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
    void 객체로_RefreshToken_저장() throws Exception {
        // Given
        String userAccount = "test";
        String token = "test";
        RefreshToken refreshToken = RefreshToken.builder()
            .id(userAccount)
            .token(token)
            .build();
        // When
        when(refreshTokenRepository.save(any())).thenReturn(refreshToken);
        RefreshToken savedToken = refreshTokenService.saveRefreshToken(refreshToken);
        // Then
        Assertions.assertThat(savedToken).isEqualTo(refreshToken);
    }

    @Test
    void 사용자_ID로_RefreshToken_조회_성공() throws Exception {
        // Given
        String userAccount = "test";
        String token = "test";
        RefreshToken refreshToken = RefreshToken.builder()
            .id(userAccount)
            .token(token)
            .build();
        // When
        when(refreshTokenRepository.findById(userAccount)).thenReturn(Optional.ofNullable(refreshToken));
        RefreshToken savedToken = refreshTokenService
            .getRefreshToken(userAccount);
        // Then
        Assertions.assertThat(refreshToken).isEqualTo(savedToken);
    }

    @Test
    void 사용자_ID로_RefreshToken_조회_실패() throws Exception {
        // Given
        String userAccount = "test";
        String token = "test";
        RefreshToken refreshToken = RefreshToken.builder()
            .id(userAccount)
            .token(token)
            .build();
        // When
        when(refreshTokenRepository.findById(userAccount)).thenReturn(Optional.empty());
        // Then
        assertThrows(InvalidTokenException.class, () -> refreshTokenService
            .getRefreshToken(userAccount));
    }

    @Test
    void 사용자_ID로_RefreshToken_삭제() throws Exception {
        // Given
        String userAccount = "test";
        // When
        refreshTokenService.deleteRefreshToken(userAccount);
        when(refreshTokenRepository.findById(userAccount)).thenReturn(Optional.empty());
        // Then
        Assertions.assertThat(refreshTokenRepository.findById(userAccount)).isEmpty();
    }
}