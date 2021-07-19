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

/**
* 사용자에 대한 서비스를 처리하는 클래스입니다.
* @author : 김기정(Luke)
**/
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private EmailValidator emailValidator = EmailValidator.getInstance();

    /**
     * 사용자 추가
     * @param request 사용자 인증 DTO
     * @return 사용자 정보 DTO
     */
    @Transactional
    public UserInfoDTO addUser(UserAuthDTO request) {
        // request 유효성 검사
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
        
        // 입력받은 내용에 따른 User 객체 생성 및 저장
        User newUser = User.builder()
            .account(request.getAccount())
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.ROLE_USER)
            .activated(true)
            .build();
        User saveUser = userRepository.save(newUser);
        
        return UserInfoDTO.from(saveUser);
    }

    /**
     * 사용자 조회
     * @param request 사용자 인증 DTO
     * @return 요청받은 사용자에 대한 정보 DTO
     */
    public UserInfoDTO getUser(UserAuthDTO request) {
        User user = userRepository.findByAccount(request.getAccount())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        return UserInfoDTO.from(user);
    }

    /**
     * 모든 사용자 목록 조회
     * @return 사용자 정보 DTO 리스트
     */
    public List<UserInfoDTO> getUsers() {
        List<UserInfoDTO> userInfoDTO = userRepository.findAll().stream()
            .map(user -> UserInfoDTO.from(user))
            .collect(Collectors.toList());
        return userInfoDTO;
    }

    /**
     * 사용자 정보 갱신
     * @param userAccount 사용자 ID
     * @param request 사용자 인증 DTO
     * @return 정보가 변경된 사용자의 사용자 정보 DTO
     */
    @Transactional
    public UserInfoDTO updateUser(String userAccount, UserAuthDTO request) {
        // request 유효성 검사
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

        // request에 따른 객체 정보 update 및 반환
        if (request.getAccount() != null && !request.getAccount().isEmpty()) {
            user.setAccount(request.getAccount());
        }
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (!emailValidator.isValid(request.getEmail())) {
                throw new NotValidEmailException("유효한 메일주소가 아닙니다.");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        userRepository.save(user);
        
        return UserInfoDTO.from(user);
    }

    /**
     * 사용자 삭제, 회원탈퇴
     * @param request 사용자 인증 DTO
     */
    @Transactional
    public void deleteUser(UserAuthDTO request) {
        User user = userRepository.findByAccount(request.getAccount())
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }

    /**
     * 사용자 객체를 UserDetails 객체로 변환 후 반환
     * @param username 사용자 ID
     * @return org.springframework.security.core.userdetails.User 객체
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findByAccount(username)
            .map(user -> createUser(username, user))
            .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    /**
     * UserDetails 객체 생성
     * @param username 사용자 ID
     * @param user 사용자 객체
     * @return org.springframework.security.core.userdetails.User 객체
     */
    private org.springframework.security.core.userdetails.User createUser(String username,
        User user) {
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }
        // 사용자 권한 확인
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return new org.springframework.security.core.userdetails.User(user.getAccount(),
            user.getPassword(),
            grantedAuthorities);
    }
}
