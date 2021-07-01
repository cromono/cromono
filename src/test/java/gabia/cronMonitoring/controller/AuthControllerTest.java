package gabia.cronMonitoring.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.request.UserInfoDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.jwt.JwtAccessDeniedHandler;
import gabia.cronMonitoring.jwt.JwtAuthenticationEntryPoint;
import gabia.cronMonitoring.jwt.TokenProvider;
import gabia.cronMonitoring.jwt.AuthService;
import gabia.cronMonitoring.service.UserService;
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
    void login() throws Exception {
        // Given
        UserInfoDTO request = UserInfoDTO.builder()
            .account("luke")
            .password("luke")
            .build();
        AccessTokenDTO response = AccessTokenDTO.builder()
            .token("test")
            .expiresAt(Instant.now())
            .build();
        // When
        when(authService.authorize(request)).thenReturn(response);
        // Then
        mockMvc.perform(post("/auth/local/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..*", response).exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void logout() throws Exception {
        // Given
        UserInfoDTO request = UserInfoDTO.builder()
            .account("luke")
            .password("luke")
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
    void register() throws Exception {
        // Given
        UserInfoDTO request = UserInfoDTO.builder()
            .account("luke")
            .password("luke")
            .name("luke")
            .email("luke@gabia.com")
            .build();
        gabia.cronMonitoring.dto.response.UserInfoDTO response = gabia.cronMonitoring.dto.response.UserInfoDTO
            .builder()
            .account("luke")
            .name("luke")
            .email("luke@gabia.com")
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
            .andExpect(jsonPath("$..*", response).exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void refreshToken() throws Exception {
        // Given
        String userAccount = "Luke";
        AccessTokenDTO response = AccessTokenDTO.builder()
            .token("test")
            .expiresAt(Instant.now())
            .build();
        // When
        when(authService.refreshAccessToken(userAccount)).thenReturn(response);
        // Then
        mockMvc.perform(post("/auth/local/refresh-token/{userAccount}", userAccount))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$..*", response).exists());
    }
}