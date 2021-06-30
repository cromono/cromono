package gabia.cronMonitoring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.request.UserInfoDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.jwt.JwtAccessDeniedHandler;
import gabia.cronMonitoring.jwt.JwtAuthenticationEntryPoint;
import gabia.cronMonitoring.jwt.TokenProvider;
import gabia.cronMonitoring.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@WithMockUser(roles = "USER")
class UserControllerTest {

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

    @Autowired
    ObjectMapper mapper;

    @Test
    void 사용자_목록_GET() throws Exception {
        // Given
        List<gabia.cronMonitoring.dto.response.UserInfoDTO> users = new ArrayList<>();
        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO1 = new gabia.cronMonitoring.dto.response.UserInfoDTO();
        userInfoDTO1.setAccount("test1");
        userInfoDTO1.setEmail("test1");
        userInfoDTO1.setName("test1");
        userInfoDTO1.setRole(UserRole.ROLE_USER);

        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO2 = new gabia.cronMonitoring.dto.response.UserInfoDTO();
        userInfoDTO2.setAccount("test2");
        userInfoDTO2.setEmail("test2");
        userInfoDTO2.setName("test2");
        userInfoDTO2.setRole(UserRole.ROLE_USER);

        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO3 = new gabia.cronMonitoring.dto.response.UserInfoDTO();
        userInfoDTO3.setAccount("test3");
        userInfoDTO3.setEmail("test3");
        userInfoDTO3.setName("test3");
        userInfoDTO3.setRole(UserRole.ROLE_USER);

        users.add(userInfoDTO1);
        users.add(userInfoDTO2);
        users.add(userInfoDTO3);

        // When
        String expectAllUsers = "$.[*]";
        when(userService.getUsers()).thenReturn(users);

        // Then
        mockMvc.perform(get("/users"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectAllUsers, users).exists())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void 사용자_정보_GET() throws Exception {
        // Given
        UserInfoDTO request = new UserInfoDTO();
        request.setAccount("test1");

        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO = new gabia.cronMonitoring.dto.response.UserInfoDTO();
        userInfoDTO.setAccount("test1");
        userInfoDTO.setEmail("test1");
        userInfoDTO.setName("test1");
        userInfoDTO.setRole(UserRole.ROLE_USER);

        // When
        String expectByUserAccount = "$.account";
        when(userService.getUser(request)).thenReturn(userInfoDTO);

        // Then
        mockMvc.perform(get("/users/{userId}", "test1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectByUserAccount, userInfoDTO).exists());
    }

    @Test
    void 사용자_정보_PATCH() throws Exception {
        // Given
        UserInfoDTO request = new UserInfoDTO();
        request.setAccount("test2");

        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO = new gabia.cronMonitoring.dto.response.UserInfoDTO();
        userInfoDTO.setAccount("test2");
        userInfoDTO.setEmail("test1");
        userInfoDTO.setName("test1");
        userInfoDTO.setRole(UserRole.ROLE_USER);

        // When
        String expectByUserAccount = "$.account";
        when(userService.updateUser("test1", request)).thenReturn(userInfoDTO);

        // Then
        mockMvc.perform(patch("/users/{userId}", "test1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectByUserAccount, userInfoDTO).exists());
    }

    @Test
    void 사용자_DELETE() throws Exception {
        // Given
        // When
        // Then
        mockMvc.perform(delete("/users/{userId}", "test1"))
            .andDo(print())
            .andExpect(status().isNoContent());
    }
}