package gabia.cronMonitoring.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import gabia.cronMonitoring.exception.user.InputNotFoundException;
import gabia.cronMonitoring.exception.user.NotValidEmailException;
import gabia.cronMonitoring.service.AuthService;
import gabia.cronMonitoring.service.UserService;
import gabia.cronMonitoring.util.jwt.JwtAccessDeniedHandler;
import gabia.cronMonitoring.util.jwt.JwtAuthenticationEntryPoint;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Autowired
    ObjectMapper mapper;

    @Test
    void login_성공() throws Exception {
        // Given
        UserAuthDTO request = UserAuthDTO.builder()
            .account("test")
            .password("test")
            .build();
        AccessTokenDTO response = AccessTokenDTO.builder()
            .token("test")
            .expiresAt(Instant.now())
            .build();
        // When
        when(authService.authenticate(request)).thenReturn(response);
        // Then
        mockMvc.perform(post("/auth/local/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(response.getToken()));
        authService.deleteRefreshToken(response.getToken());
    }

    @Test
    void login_실패() throws Exception {
        // Given
        UserAuthDTO request = UserAuthDTO.builder()
            .account("test")
            .password("test")
            .build();
        // When
        when(authService.authenticate(request)).thenReturn(null);
        // Then
        mockMvc.perform(post("/auth/local/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void logout() throws Exception {
        // Given
        UserAuthDTO request = UserAuthDTO.builder()
            .account("test")
            .password("test")
            .build();
        // When
        // Then
        mockMvc.perform(post("/auth/local/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    void register_성공() throws Exception {
        // Given
        UserAuthDTO request = UserAuthDTO.builder()
            .account("test")
            .password("test")
            .name("test")
            .email("test@gabia.com")
            .build();
        UserInfoDTO response = UserInfoDTO
            .builder()
            .account("test")
            .name("test")
            .email("test@gabia.com")
            .role(UserRole.ROLE_USER)
            .build();
        // When
        when(userService.addUser(request)).thenReturn(response);
        // Then
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").value(response));
    }

    @Test
    void 내용_미기입시_register_실패() throws Exception {
        // Given
        UserAuthDTO request = UserAuthDTO.builder().build();
        // When
        when(userService.addUser(request)).thenThrow(InputNotFoundException.class);
        // Then
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    void 유효하지_않은_이메일_기입시_register_실패() throws Exception {
        // Given
        UserAuthDTO request = UserAuthDTO.builder()
            .account("test")
            .name("test")
            .email("test")
            .role(UserRole.ROLE_USER)
            .build();
        // When
        when(userService.addUser(request)).thenThrow(NotValidEmailException.class);
        // Then
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void refreshToken_성공() throws Exception {
        // Given
        String userAccount = "test";
        String token = "test";
        AccessTokenDTO response = AccessTokenDTO.builder()
            .token(token)
            .expiresAt(Instant.now())
            .build();
        // When
        when(authService.getCurrentUser())
            .thenReturn(UserInfoDTO.builder().account(userAccount).build());
        when(authService.refreshAccessToken(userAccount, token)).thenReturn(response);
        // Then
        mockMvc.perform(post("/auth/local/refresh-token/{oldToken}", token))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value(response.getToken()));
        authService.deleteRefreshToken(response.getToken());
    }

    @Test
    @WithMockUser(roles = "USER")
    void refreshToken_실패() throws Exception {
        // Given
        String userAccount = "test";
        String token = "test";
        AccessTokenDTO response = AccessTokenDTO.builder()
            .token(token)
            .expiresAt(Instant.now())
            .build();
        // When
        when(authService.getCurrentUser())
            .thenReturn(UserInfoDTO.builder().account(userAccount).build());
        when(authService.refreshAccessToken(userAccount, token)).thenThrow(InvalidTokenException.class);
        // Then
        mockMvc.perform(post("/auth/local/refresh-token/{oldToken}", token))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}