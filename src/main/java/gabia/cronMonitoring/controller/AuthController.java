package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.request.UserAccessDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.service.AuthService;
import gabia.cronMonitoring.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.jwt.JwtFilter;
import gabia.cronMonitoring.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/local/login")
    public ResponseEntity<AccessTokenDTO> login(@Valid @RequestBody UserAccessDTO request) {

        AccessTokenDTO tokenDTO = authService.login(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDTO.getToken());

        return new ResponseEntity(tokenDTO, httpHeaders, HttpStatus.OK);
    }

    // TODO: 로그아웃 구현
//    @PostMapping("/local/logout")
//    public ResponseEntity<TokenDTO> logout() {
//
//    }

    @PostMapping("/register")
    public ResponseEntity<AccessTokenDTO> register(@Valid @RequestBody UserAccessDTO request) {
        UserInfoDTO userInfoDTO = userService.addUser(request);
        ResponseEntity responseEntity = new ResponseEntity(userInfoDTO, HttpStatus.CREATED);
        return responseEntity;
    }
}
