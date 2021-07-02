package gabia.cronMonitoring.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
import gabia.cronMonitoring.service.AuthService;
import gabia.cronMonitoring.service.RefreshTokenService;
import gabia.cronMonitoring.service.UserService;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active:common")
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
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
        userService.addUser(request);
        // When
        AccessTokenDTO response = authService.authenticate(request);
        // Then
        String token = refreshTokenRepository.findById(response.getToken()).get().getId();
        Assertions.assertThat(response.getToken()).isEqualTo(token);
    }

    @Test
    void 로그아웃() throws Exception {
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
        userService.addUser(request);
        AccessTokenDTO response = authService.authenticate(request);
        // When
        // Then
        assertDoesNotThrow(() -> authService.unauthenticate(response.getToken()));
    }

    @Test
    void 엑세스_토큰_재발급() throws Exception {
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
        userService.addUser(request);
        AccessTokenDTO response = authService.authenticate(request);
        // When
        // Then
        assertDoesNotThrow(() -> authService.refreshAccessToken(account, response.getToken()));
    }
}