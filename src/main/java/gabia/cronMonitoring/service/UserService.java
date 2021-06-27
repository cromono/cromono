package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.UserDTO;
import gabia.cronMonitoring.dto.UserDTO.Response;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.UserDetailsImpl;
import gabia.cronMonitoring.exception.user.ExistingInputException;
import gabia.cronMonitoring.exception.user.InputNotFoundException;
import gabia.cronMonitoring.exception.user.UserNotFoundException;
import gabia.cronMonitoring.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService /*implements UserDetailsService*/ {

    private final UserRepository userRepository;

    @Transactional
    public UserDTO.Response addUser(UserDTO.Request request) {
        if (request.getAccount().isEmpty()) {
            throw new InputNotFoundException("ID를 입력하지 않았습니다.");
        }
        if (request.getName().isEmpty()) {
            throw new InputNotFoundException("이름을 입력하지 않았습니다.");
        }
        if (request.getEmail().isEmpty()) {
            throw new InputNotFoundException("메일 주소를 입력하지 않았습니다.");
        }
        if (request.getPassword().isEmpty()) {
            throw new InputNotFoundException("패스워드를 입력하지 않았습니다.");
        }
        userRepository.findByAccount(request.getAccount()).ifPresent(none -> {
            throw new ExistingInputException("이미 등록된 ID입니다.");
        });
        userRepository.findByEmail(request.getEmail()).ifPresent(none -> {
            throw new ExistingInputException("이미 등록된 메일 주소입니다.");
        });
        User newUser = User.builder()
            .account(request.getAccount())
            .name(request.getName())
            .email(request.getEmail())
            .password(request.getPassword())
            .role(UserRole.USER)
            .build();

        return UserDTO.Response.from(userRepository.save(newUser));
    }

    public UserDTO.Response getUser(UserDTO.Request request) {
        User user = userRepository.findByAccount(request.getAccount())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        return UserDTO.Response.from(user);
    }

    public List<UserDTO.Response> getUsers() {
        List<UserDTO.Response> response = userRepository.findAll().stream()
            .map(user -> UserDTO.Response.from(user))
            .collect(Collectors.toList());
        return response;
    }

    @Transactional
    public UserDTO.Response updateUser(String userAccount, UserDTO.Request request) {
        User user = userRepository.findByAccount(userAccount)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        if (!userAccount.equals(request.getAccount())) {
            userRepository.findByAccount(request.getAccount()).ifPresent(none -> {
                throw new ExistingInputException("이미 등록된 ID입니다.");
            });
        }

        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(none -> {
                throw new ExistingInputException("이미 등록된 메일 주소입니다.");
            });
        }
        if (!request.getAccount().isEmpty()) {
            user.setAccount(request.getAccount());
        }
        if (!request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (!request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (!request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        userRepository.save(user);

        return UserDTO.Response.from(user);
    }

    @Transactional
    public void deleteUser(UserDTO.Request request) {
        User user = userRepository.findByAccount(request.getAccount())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByAccount(username).map(user -> new UserDetailsImpl())
//    }
}
