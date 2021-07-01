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
import gabia.cronMonitoring.util.jwt.JwtAccessDeniedHandler;
import gabia.cronMonitoring.util.jwt.JwtAuthenticationEntryPoint;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import gabia.cronMonitoring.service.AuthService;
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
        UserAuthDTO request = UserAuthDTO.builder()
            .account("luke")
            .password("luke")
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
            .andExpect(jsonPath("$..*", response).exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void logout() throws Exception {
        // Given
        UserAuthDTO request = UserAuthDTO.builder()
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
        UserAuthDTO request = UserAuthDTO.builder()
            .account("luke")
            .password("luke")
            .name("luke")
            .email("luke@gabia.com")
            .build();
        UserInfoDTO response = UserInfoDTO
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