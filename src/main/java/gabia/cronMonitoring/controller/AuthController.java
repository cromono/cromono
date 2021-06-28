package gabia.cronMonitoring.controller;

import gabia.cronMonitoring.dto.UserDTO.Response;
import gabia.cronMonitoring.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import gabia.cronMonitoring.dto.UserDTO;
import gabia.cronMonitoring.dto.TokenDTO;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> authorize(@Valid @RequestBody UserDTO.Request request) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity(new TokenDTO(jwt), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenDTO> signup(@Valid @RequestBody UserDTO.Request request) {
        Response response = userService.addUser(request);
        ResponseEntity responseEntity = new ResponseEntity(response, HttpStatus.CREATED);
        return responseEntity;
    }
}
