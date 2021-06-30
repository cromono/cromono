package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.request.RefreshTokenDTO;
import gabia.cronMonitoring.dto.request.UserInfoDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.jwt.JwtFilter;
import gabia.cronMonitoring.service.AuthService;
import gabia.cronMonitoring.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/local/login")
    public ResponseEntity<AccessTokenDTO> login(@Valid @RequestBody UserInfoDTO request) {

        AccessTokenDTO tokenDTO = authService.login(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDTO.getToken());

        return new ResponseEntity(tokenDTO, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/local/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenDTO request) {
        authService.logout(request);
        return new ResponseEntity("Refresh Token Deleted Successfully!", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AccessTokenDTO> register(@Valid @RequestBody UserInfoDTO request) {
        gabia.cronMonitoring.dto.response.UserInfoDTO userInfoDTO = userService.addUser(request);
        return new ResponseEntity(userInfoDTO, HttpStatus.CREATED);
    }
}
