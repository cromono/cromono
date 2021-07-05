package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.repository.RefreshTokenRepository;
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
        authService.deleteRefreshToken(response.getToken());
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
        authService.deleteRefreshToken(response.getToken());
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
        // When
        AccessTokenDTO response = authService.authenticate(request);
        // Then
        assertDoesNotThrow(() -> authService.refreshAccessToken(account, response.getToken()));
        authService.deleteRefreshToken(response.getToken());
    }

    @Test
    void 현재_사용자_확인() throws Exception {
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
        UserInfoDTO currentUser = authService.getCurrentUser();
        // Then
        Assertions.assertThat(currentUser.getAccount()).isEqualTo(account);
        authService.deleteRefreshToken(response.getToken());
    }
}