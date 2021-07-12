package gabia.cronMonitoring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.exception.user.ExistingInputException;
import gabia.cronMonitoring.exception.user.UserNotFoundException;
import gabia.cronMonitoring.util.jwt.JwtAccessDeniedHandler;
import gabia.cronMonitoring.util.jwt.JwtAuthenticationEntryPoint;
import gabia.cronMonitoring.util.jwt.TokenProvider;
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
    public void 사용자_목록_GET_성공() throws Exception {
        // Given
        List<UserInfoDTO> users = new ArrayList<>();
        UserInfoDTO userInfoDTO1 = new UserInfoDTO();
        userInfoDTO1.setAccount("test1");
        userInfoDTO1.setEmail("test1");
        userInfoDTO1.setName("test1");
        userInfoDTO1.setRole(UserRole.ROLE_USER);

        UserInfoDTO userInfoDTO2 = new UserInfoDTO();
        userInfoDTO2.setAccount("test2");
        userInfoDTO2.setEmail("test2");
        userInfoDTO2.setName("test2");
        userInfoDTO2.setRole(UserRole.ROLE_USER);

        UserInfoDTO userInfoDTO3 = new UserInfoDTO();
        userInfoDTO3.setAccount("test3");
        userInfoDTO3.setEmail("test3");
        userInfoDTO3.setName("test3");
        userInfoDTO3.setRole(UserRole.ROLE_USER);

        users.add(userInfoDTO1);
        users.add(userInfoDTO2);
        users.add(userInfoDTO3);

        // When
        when(userService.getUsers()).thenReturn(users);

        // Then
        mockMvc.perform(get("/users"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value(users.get(0)))
            .andExpect(jsonPath("$[1]").value(users.get(1)))
            .andExpect(jsonPath("$[2]").value(users.get(2)))
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void 사용자_정보_GET_성공() throws Exception {
        // Given
        UserAuthDTO request = new UserAuthDTO();
        request.setAccount("test1");

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setAccount("test1");
        userInfoDTO.setEmail("test1");
        userInfoDTO.setName("test1");
        userInfoDTO.setRole(UserRole.ROLE_USER);

        // When
        when(userService.getUser(request)).thenReturn(userInfoDTO);

        // Then
        mockMvc.perform(get("/users/{userId}", "test1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(userInfoDTO));
    }

    @Test
    public void 존재하지_않는_사용자_정보_조회시_GET_예외() throws Exception {
        // Given
        UserAuthDTO request = new UserAuthDTO();
        request.setAccount("test1");
        // When
        when(userService.getUser(request)).thenThrow(UserNotFoundException.class);
        // Then
        mockMvc.perform(get("/users/{userId}", "test1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 사용자_정보_PATCH_성공() throws Exception {
        // Given
        UserAuthDTO request = new UserAuthDTO();
        request.setAccount("test2");

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setAccount("test2");
        userInfoDTO.setEmail("test1");
        userInfoDTO.setName("test1");
        userInfoDTO.setRole(UserRole.ROLE_USER);

        // When
        String expectByUserAccount = "$";
        when(userService.updateUser("test1", request)).thenReturn(userInfoDTO);

        // Then
        mockMvc.perform(patch("/users/{userId}", "test1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath(expectByUserAccount).value(userInfoDTO));
    }

    @Test
    public void 존재하지_않는_사용자_정보_갱신시_PATCH_예외() throws Exception {
        // Given
        String userAccount = "test1";
        UserAuthDTO request = new UserAuthDTO();
        request.setAccount("test2");
        // When
        when(userService.updateUser(userAccount, request)).thenThrow(UserNotFoundException.class);
        // Then
        mockMvc.perform(patch("/users/{userId}", "test1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void 등록된_정보로_사용자_정보_갱신시_PATCH_예외() throws Exception {
        // Given
        String userAccount = "test1";
        UserAuthDTO request = new UserAuthDTO();
        request.setAccount("test2");
        // When
        when(userService.updateUser(userAccount, request)).thenThrow(ExistingInputException.class);
        // Then
        mockMvc.perform(patch("/users/{userId}", "test1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @Test
    public void 사용자_DELETE_성공() throws Exception {
        // Given
        UserAuthDTO request = new UserAuthDTO();
        request.setAccount("test1");
        // When
        doNothing().when(userService).deleteUser(request);
        // Then
        mockMvc.perform(delete("/users/{userId}", "test1"))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    public void 미등록_사용자_DELETE_예외() throws Exception {
        // Given
        UserAuthDTO request = new UserAuthDTO();
        request.setAccount("test1");
        // When
        doThrow(UserNotFoundException.class).when(userService).deleteUser(request);
        // Then
        mockMvc.perform(delete("/users/{userId}", "test1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}