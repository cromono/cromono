package gabia.cronMonitoring.service;

import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.entity.RefreshToken;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import java.time.Instant;
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

    public AccessTokenDTO authenticate(UserAuthDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);
        refreshTokenService.deleteRefreshToken(request.getAccount());
        refreshTokenService.generateRefreshToken(request.getAccount());

        return AccessTokenDTO.builder()
            .token(jwt)
            .expiresAt(Instant.now().plusMillis(tokenProvider.getTokenValidityInMilliseconds()))
            .build();
    }

    public void unauthenticate(String userAccount) {
        refreshTokenService.deleteRefreshToken(userAccount);
        SecurityContextHolder.clearContext();
    }

    public AccessTokenDTO refreshAccessToken(String userAccount) {
        RefreshToken refreshToken = refreshTokenService.getRefreshTokenByUserAccount(userAccount);
        refreshTokenService.validateRefreshToken(refreshToken.getToken());
        refreshTokenService.deleteRefreshToken(userAccount);
        refreshTokenService.generateRefreshToken(userAccount);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = tokenProvider.createToken(authentication);

        return AccessTokenDTO.builder()
            .token(jwt)
            .expiresAt(Instant.now().plusMillis(tokenProvider.getTokenValidityInMilliseconds()))
            .build();
    }
}
