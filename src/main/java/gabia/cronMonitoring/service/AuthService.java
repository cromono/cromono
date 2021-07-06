package gabia.cronMonitoring.service;

import gabia.cronMonitoring.business.RefreshTokenBusiness;
import gabia.cronMonitoring.dto.request.UserAuthDTO;
import gabia.cronMonitoring.dto.response.AccessTokenDTO;
import gabia.cronMonitoring.dto.response.UserInfoDTO;
import gabia.cronMonitoring.entity.RefreshToken;
import gabia.cronMonitoring.exception.auth.InvalidTokenException;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenBusiness refreshTokenBusiness;

    public AccessTokenDTO authenticate(UserAuthDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AccessTokenDTO tokenDto = tokenProvider.createToken(authentication);
        refreshTokenBusiness.saveRefreshToken(RefreshToken.builder()
            .id(request.getAccount())
            .token(tokenDto.getRefreshToken())
            .build());

        return tokenDto;
    }

    public void unauthenticate(String userAccount) {
        refreshTokenBusiness.deleteRefreshToken(userAccount);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

    public AccessTokenDTO reissueAccessToken(String accessToken, String refreshToken) {

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token입니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        RefreshToken savedRefreshToken = refreshTokenBusiness
            .getRefreshToken(authentication.getName());

        if (!refreshToken.equals(savedRefreshToken.getToken())) {
            throw new InvalidTokenException("Refresh Token의 유저 정보가 일치하지 않습니다.");
        }

        AccessTokenDTO resultTokenDto = tokenProvider.createToken(authentication);

        savedRefreshToken.setToken(resultTokenDto.getRefreshToken());

        refreshTokenBusiness.saveRefreshToken(savedRefreshToken);

        return resultTokenDto;
    }

    public UserInfoDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDTO user = UserInfoDTO.builder()
            .account(authentication.getName())
            .build();
        return user;
    }

    public void deleteRefreshToken(String userAccount) {
        refreshTokenBusiness.deleteRefreshToken(userAccount);
    }
}
