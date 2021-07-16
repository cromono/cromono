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

/**
* 인증과 관련된 서비스를 처리하는 클래스입니다.
* @author : 김기정(Luke)
**/
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenBusiness refreshTokenBusiness;

    /**
     * 사용자 로그인
     * @param request 사용자 인증 DTO
     * @return 액세스 토큰 DTO
     */
    public AccessTokenDTO authenticate(UserAuthDTO request) {
        // 입력받은 인증정보에 맞는 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.getAccount(), request.getPassword());

        // 인증 정보 검증 후 Security Context에 저장
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Access Token 및 Refresh Token 생성 및 저장
        AccessTokenDTO tokenDto = tokenProvider.createToken(authentication);
        refreshTokenBusiness.saveRefreshToken(RefreshToken.builder()
            .id(request.getAccount())
            .token(tokenDto.getRefreshToken())
            .build());

        return tokenDto;
    }

    /**
     * 사용자 로그아웃
     * @param userAccount 로그아웃 할 사용자 ID
     */
    public void unauthenticate(String userAccount) {
        // Security Context 및 Redis에서 토큰 정보 삭제
        refreshTokenBusiness.deleteRefreshToken(userAccount);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

    /**
     * 엑세스 토큰 재발급 및 리프레시 토큰 갱신
     * @param accessToken 만료된 엑세스 토큰
     * @param refreshToken 리프레시 토큰
     * @return 엑세스 토큰 DTO
     */
    public AccessTokenDTO reissueAccessToken(String refreshToken) {
        
        // JWT 유효성 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token입니다.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RefreshToken savedRefreshToken = refreshTokenBusiness
            .getRefreshToken(authentication.getName());

        if (!refreshToken.equals(savedRefreshToken.getToken())) {
            throw new InvalidTokenException("Refresh Token의 유저 정보가 일치하지 않습니다.");
        }
        
        // 신규 토큰 발급 및 저장
        AccessTokenDTO resultTokenDto = tokenProvider.createToken(authentication);
        savedRefreshToken.setToken(resultTokenDto.getRefreshToken());
        refreshTokenBusiness.saveRefreshToken(savedRefreshToken);

        return resultTokenDto;
    }

    /**
     * 현재 인증된 사용자 조회
     * @return 사용자 정보 DTO
     */
    public UserInfoDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfoDTO user = UserInfoDTO.builder()
            .account(authentication.getName())
            .build();
        return user;
    }

    /**
     * 사용자의 리프레시 토큰 삭제
     * @param userAccount 리프레시 토큰을 삭제할 사용자 ID
     */
    public void deleteRefreshToken(String userAccount) {
        refreshTokenBusiness.deleteRefreshToken(userAccount);
    }
}
