package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gabia.cronMonitoring.business.RefreshTokenBusiness;
import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
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
    RefreshTokenBusiness refreshTokenBusiness;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @Transactional
    void 로그인_성공() throws Exception {
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
        String token = refreshTokenRepository.findById(account).get().getToken();
        Assertions.assertThat(response.getRefreshToken()).isEqualTo(token);
        authService.deleteRefreshToken(account);
        authService.unauthenticate(account);
    }

    @Test
    @Transactional
    void 로그아웃_성공() throws Exception {
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
        authService.deleteRefreshToken(account);
        authService.unauthenticate(account);
    }

    @Test
    @Transactional
    void 엑세스_토큰_재발급_성공() throws Exception {
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
        assertDoesNotThrow(() -> authService.reissueAccessToken(
            response.getRefreshToken()));
        authService.deleteRefreshToken(account);
        authService.unauthenticate(account);
    }

    @Test
    @Transactional
    void 리프레시_토큰의_유저_정보_비일치로_인한_엑세스_토큰_재발급_실패() throws Exception {
        // Given
        String account = "test123";
        String name = "test123";
        String email = "test@gabia.com";
        String password = "test";
        String anotherAccount = "testtest";
        String anotherName = "testtest";
        String anotherEmail = "testtest@gabia.com";
        String anotherPassword = "testtest";
        UserAuthDTO request = UserAuthDTO.builder()
            .account(account)
            .name(name)
            .email(email)
            .password(password)
            .role(UserRole.ROLE_USER)
            .build();
        UserAuthDTO anotherRequest = UserAuthDTO.builder()
            .account(anotherAccount)
            .name(anotherName)
            .email(anotherEmail)
            .password(anotherPassword)
            .role(UserRole.ROLE_USER)
            .build();
        userService.addUser(request);
        userService.addUser(anotherRequest);
        // When
        AccessTokenDTO anotherResponse = authService.authenticate(anotherRequest);
        String refreshToken = anotherResponse.getRefreshToken().toString();
        authService.unauthenticate(anotherAccount);
        authService.deleteRefreshToken(anotherAccount);
        authService.authenticate(request);
        // Then
        authService.deleteRefreshToken(account);
        authService.deleteRefreshToken(anotherAccount);
        assertThrows(InvalidTokenException.class, () -> authService.reissueAccessToken(
            refreshToken));
        authService.deleteRefreshToken(account);
        authService.deleteRefreshToken(anotherAccount);
        authService.unauthenticate(account);
        authService.unauthenticate(anotherAccount);
    }

    @Test
    @Transactional
    void 유효하지_않은_리프레시_토큰으로_인한_엑세스_토큰_재발급_실패() throws Exception {
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
        authService.unauthenticate(account);
        // Then
        assertThrows(InvalidTokenException.class,
            () -> authService.reissueAccessToken(
                "notvalidtoken"));
        authService.deleteRefreshToken(account);
        authService.unauthenticate(account);
    }

    @Test
    @Transactional
    void 현재_사용자_확인_성공() throws Exception {
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
        authService.deleteRefreshToken(account);
        authService.unauthenticate(account);
    }
}