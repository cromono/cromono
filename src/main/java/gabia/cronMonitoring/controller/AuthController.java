package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.util.jwt.JwtFilter;
import gabia.cronMonitoring.service.AuthService;
import gabia.cronMonitoring.service.UserService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/local/login")
    public ResponseEntity<AccessTokenDTO> login(@Valid @RequestBody UserAuthDTO request) {

        AccessTokenDTO tokenDTO = authService.authenticate(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDTO.getToken());

        return new ResponseEntity(tokenDTO, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/local/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody UserAuthDTO request) {
        authService.unauthenticate(request.getAccount());
        return new ResponseEntity("Refresh Token Deleted Successfully!", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AccessTokenDTO> register(@Valid @RequestBody UserAuthDTO request) {
        UserInfoDTO userInfoDTO = userService.addUser(request);
        return new ResponseEntity(userInfoDTO, HttpStatus.CREATED);
    }

    @PostMapping("/local/refresh-token/{oldToken}")
    public ResponseEntity<AccessTokenDTO> refreshToken(@NotBlank @PathVariable(name = "oldToken") String jwt) {
        AccessTokenDTO accessTokenDTO = authService.refreshAccessToken(authService.getCurrentUser().getAccount(), jwt);
        return new ResponseEntity(accessTokenDTO, HttpStatus.CREATED);
    }
}
