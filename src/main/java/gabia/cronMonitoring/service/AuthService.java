package gabia.cronMonitoring.service;

import gabia.cronMonitoring.business.RefreshTokenService;
import gabia.cronMonitoring.dto.request.RefreshTokenDTO;
import gabia.cronMonitoring.dto.request.UserInfoDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenService refreshTokenService;

    public AccessTokenDTO login(UserInfoDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        return AccessTokenDTO.builder()
            .token(jwt)
            .refreshToken(refreshTokenService.generateRefreshToken().getToken())
            .build();
    }

    public void logout(RefreshTokenDTO request) {
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
        SecurityContextHolder.clearContext();
    }

    public AccessTokenDTO refreshAccessToken(RefreshTokenDTO request) {
        refreshTokenService.validateRefreshToken(request.getRefreshToken());
        String token = tokenProvider.createTokenWithRefreshToken(request.getRefreshToken());
        return AccessTokenDTO.builder()
            .token(token)
            .refreshToken(request.getRefreshToken())
            .build();
    }
}
