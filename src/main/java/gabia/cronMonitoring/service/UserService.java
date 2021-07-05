package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.entity.Enum.UserRole;
import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.exception.user.ExistingInputException;
import gabia.cronMonitoring.exception.user.InputNotFoundException;
import gabia.cronMonitoring.exception.user.NotValidEmailException;
import gabia.cronMonitoring.exception.user.UserNotFoundException;
import gabia.cronMonitoring.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Transactional
    public UserInfoDTO addUser(UserAuthDTO request) {
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
        if (!emailValidator.isValid(request.getEmail())) {
            throw new NotValidEmailException("유효한 메일주소가 아닙니다.");
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
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();

        return UserInfoDTO.from(userRepository.save(newUser));
    }

    public UserInfoDTO getUser(UserAuthDTO request) {
        User user = userRepository.findByAccount(request.getAccount())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        return UserInfoDTO.from(user);
    }

    public List<UserInfoDTO> getUsers() {
        List<UserInfoDTO> userInfoDTO = userRepository.findAll().stream()
            .map(user -> UserInfoDTO.from(user))
            .collect(Collectors.toList());
        return userInfoDTO;
    }

    @Transactional
    public UserInfoDTO updateUser(String userAccount, UserAuthDTO request) {
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

        return UserInfoDTO.from(user);
    }

    @Transactional
    public void deleteUser(UserAuthDTO request) {
        User user = userRepository.findByAccount(request.getAccount())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findByAccount(username)
            .map(user -> createUser(username, user))
            .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(String username,
        User user) {
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return new org.springframework.security.core.userdetails.User(user.getAccount(),
            user.getPassword(),
            grantedAuthorities);
    }
}
