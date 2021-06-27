package gabia.cronMonitoring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import gabia.cronMonitoring.dto.UserDTO;
import gabia.cronMonitoring.dto.UserDTO.Response;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.exception.user.ExistingInputException;
import gabia.cronMonitoring.exception.user.InputNotFoundException;
import gabia.cronMonitoring.exception.user.UserNotFoundException;
import gabia.cronMonitoring.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;

    @Test
    public void 회원가입() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String email = "test@gabia.com";
        String password = "test";
        User newUser = User.builder()
            .account(account)
            .name(name)
            .email(email)
            .password(password)
            .role(UserRole.USER)
            .build();
        when(userRepository.save(any())).thenReturn(newUser);
        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        Response response = userService.addUser(request);
        when(userRepository.findByAccount(response.getAccount())).thenReturn(Optional.of(newUser));
        // Then
        Assertions.assertThat(newUser).isEqualTo(userRepository.findByAccount(account).get());
    }

    @Test
    public void ID_미입력시_회원가입_예외() throws Exception {
        // Given
        String account = "";
        String name = "test";
        String email = "test@gabia.com";
        String password = "test";

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        // Then
        assertThrows(InputNotFoundException.class, () -> userService.addUser(request));
    }

    @Test
    public void 이름_미입력시_회원가입_예외() throws Exception {
        // Given
        String account = "test";
        String name = "";
        String email = "test@gabia.com";
        String password = "test";

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        // Then
        assertThrows(InputNotFoundException.class, () -> userService.addUser(request));
    }

    @Test
    public void 메일_미입력시_회원가입_예외() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String email = "";
        String password = "test";

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        // Then
        assertThrows(InputNotFoundException.class, () -> userService.addUser(request));
    }

    @Test
    public void 패스워드_미입력시_회원가입_예외() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String email = "test@gabia.com";
        String password = "";

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        // Then
        assertThrows(InputNotFoundException.class, () -> userService.addUser(request));
    }

    @Test
    public void 이미_등록된_ID_가입시_예외() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String email = "test@gabia.com";
        String password = "test";
        User newUser = User.builder()
            .account(account)
            .name(name)
            .email(email)
            .password(password)
            .role(UserRole.USER)
            .build();

        when(userRepository.save(any())).thenReturn(newUser);
        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        userService.addUser(request);
        when(userRepository.findByAccount(account)).thenReturn(Optional.of(newUser));
        // Then
        assertThrows(ExistingInputException.class, () -> userService.addUser(request));
    }

    @Test
    public void 이미_등록된_이메일_가입시_예외() throws Exception {
        // Given
        String account = "test";
        String newAccount = "test1";
        String name = "test";
        String email = "test@gabia.com";
        String password = "test";
        User newUser = User.builder()
            .account(account)
            .name(name)
            .email(email)
            .password(password)
            .role(UserRole.USER)
            .build();

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(newAccount);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        when(userRepository.save(any())).thenReturn(newUser);
        userService.addUser(request);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(newUser));
        // Then
        assertThrows(ExistingInputException.class, () -> userService.addUser(request));
    }

    @Test
    public void 사용자_목록_조회() throws Exception {
        // Given
        List<User> users = new ArrayList<>();
        users.add(User.builder()
            .account("test1")
            .name("test1")
            .email("test1")
            .password("test1")
            .role(UserRole.USER)
            .build());
        users.add(User.builder()
            .account("test2")
            .name("test2")
            .email("test2")
            .password("test2")
            .role(UserRole.USER)
            .build());
        users.add(User.builder()
            .account("test3")
            .name("test3")
            .email("test3")
            .password("test3")
            .role(UserRole.USER)
            .build());

        List<Response> userDTOs = new ArrayList<>();
        userDTOs.add(new UserDTO.Response("test1"));
        userDTOs.add(new UserDTO.Response("test2"));
        userDTOs.add(new UserDTO.Response("test3"));
        // When
        when(userRepository.findAll()).thenReturn(users);
        List<Response> savedUsers = userService.getUsers();
        // Then
        Assertions.assertThat(savedUsers).isEqualTo(userDTOs);
    }

    @Test
    public void 사용자_정보_수정() throws Exception {
        // Given
        String account = "test";
        String oldName = "test";
        String newName = "test1";
        String email = "test@gabia.com";
        String password = "test";
        User user = User.builder()
            .account(account)
            .name(oldName)
            .email(email)
            .password(password)
            .role(UserRole.USER)
            .build();

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(newName);
        request.setEmail(email);
        request.setPassword(password);
        // When
        when(userRepository.findByAccount(account)).thenReturn(Optional.of(user));
        Response response = userService.updateUser(request);
        when(userRepository.findByAccount(response.getAccount())).thenReturn(Optional.of(user));
        // Then
        Assertions.assertThat(userRepository.findByAccount(account).get().getName())
            .isEqualTo(newName);
    }

    @Test
    public void 미등록_ID_수정시_예외() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String email = "test@gabia.com";
        String password = "test";

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        when(userRepository.findByAccount(account)).thenReturn(Optional.empty());
        // Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(request));
    }

    @Test
    public void 이미_등록된_ID로_수정시_예외() throws Exception {
        // Given
        String oldAccount = "test";
        String newAccount = "test1";
        String name = "test";
        String email = "test@gabia.com";
        String password = "test";
        User user = User.builder()
            .account(oldAccount)
            .name(name)
            .email(email)
            .password(password)
            .role(UserRole.USER)
            .build();

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(newAccount);
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        // When
        when(userRepository.findByAccount(newAccount)).thenReturn(Optional.of(user));
        // Then
        assertThrows(ExistingInputException.class, () -> userService.updateUser(request));
    }

    @Test
    public void 이미_등록된_메일로_수정시_예외() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String oldEmail = "test@gabia.com";
        String newEmail = "test1@gabia.com";
        String password = "test";
        User user = User.builder()
            .account(account)
            .name(name)
            .email(oldEmail)
            .password(password)
            .role(UserRole.USER)
            .build();

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(newEmail);
        request.setPassword(password);
        // When
        when(userRepository.findByAccount(account)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(user));
        // Then
        assertThrows(ExistingInputException.class, () -> userService.updateUser(request));
    }

    @Test
    public void 회원탈퇴() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String oldEmail = "test@gabia.com";
        String newEmail = "test1@gabia.com";
        String password = "test";
        User user = User.builder()
            .account(account)
            .name(name)
            .email(oldEmail)
            .password(password)
            .role(UserRole.USER)
            .build();

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(newEmail);
        request.setPassword(password);
        // When
        when(userRepository.findByAccount(request.getAccount()))
            .thenReturn(Optional.of(user));
        userService.deleteUser(request);
        when(userRepository.findByAccount(request.getAccount()))
            .thenReturn(Optional.empty());
        // Then
        Assertions.assertThat(userRepository.findByAccount(account)).isEmpty();
    }

    @Test
    public void 미등록_사용자_탈퇴시_예외() throws Exception {
        // Given
        String account = "test";
        String name = "test";
        String oldEmail = "test@gabia.com";
        String newEmail = "test1@gabia.com";
        String password = "test";

        UserDTO.Request request = new UserDTO.Request();
        request.setAccount(account);
        request.setName(name);
        request.setEmail(newEmail);
        request.setPassword(password);
        // When
        when(userRepository.findByAccount(request.getAccount()))
            .thenReturn(Optional.empty());
        // Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(request));
    }
}