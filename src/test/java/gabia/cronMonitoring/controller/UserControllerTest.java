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
import gabia.cronMonitoring.dto.UserDTO.Request;
import gabia.cronMonitoring.dto.UserDTO.Response;
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
        List<Response> users = new ArrayList<>();
        Response response1 = new Response();
        response1.setAccount("test1");
        response1.setEmail("test1");
        response1.setName("test1");
        response1.setRole(UserRole.ROLE_USER);

        Response response2 = new Response();
        response2.setAccount("test2");
        response2.setEmail("test2");
        response2.setName("test2");
        response2.setRole(UserRole.ROLE_USER);

        Response response3 = new Response();
        response3.setAccount("test3");
        response3.setEmail("test3");
        response3.setName("test3");
        response3.setRole(UserRole.ROLE_USER);

        users.add(response1);
        users.add(response2);
        users.add(response3);

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
        Request request = new Request();
        request.setAccount("test1");

        Response response = new Response();
        response.setAccount("test1");
        response.setEmail("test1");
        response.setName("test1");
        response.setRole(UserRole.ROLE_USER);

        // When
        String expectByUserAccount = "$.account";
        when(userService.getUser(request)).thenReturn(response);

        // Then
        mockMvc.perform(get("/users/{userId}", "test1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectByUserAccount, response).exists());
    }

    @Test
    void 사용자_정보_PATCH() throws Exception {
        // Given
        Request request = new Request();
        request.setAccount("test2");

        Response response = new Response();
        response.setAccount("test2");
        response.setEmail("test1");
        response.setName("test1");
        response.setRole(UserRole.ROLE_USER);

        // When
        String expectByUserAccount = "$.account";
        when(userService.updateUser("test1", request)).thenReturn(response);

        // Then
        mockMvc.perform(patch("/users/{userId}", "test1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectByUserAccount, response).exists());
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