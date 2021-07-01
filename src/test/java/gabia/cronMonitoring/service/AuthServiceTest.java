package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    TokenProvider tokenProvider;

    @Mock
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void 로그인() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String email = "test@gabia.com";
        String password = "test";
        UserAuthDTO request = UserAuthDTO.builder()
            .account(account)
            .name(name)
            .email(email)
            .password(password)
            .role(UserRole.ROLE_USER)
            .build();

        // When
        AccessTokenDTO response = authService.authenticate(request);
//        when(re)
        // Then
//        Assertions.assertThat(response).isEqualTo()
    }

    @Test
    void 로그아웃() throws Exception {
        // Given

        // When

        // Then
    }

    @Test
    void 엑세스_토큰_재발급() throws Exception {
        // Given

        // When
        
        // Then
    }
}