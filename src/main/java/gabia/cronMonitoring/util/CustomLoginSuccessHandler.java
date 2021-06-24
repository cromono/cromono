package gabia.cronMonitoring.util;

import gabia.cronMonitoring.entity.User;
import gabia.cronMonitoring.entity.UserDetailsImpl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

//    @Override
//    public void onAuthenticationSuccess(final HttpServletRequest request,
//        final HttpServletResponse response, final Authentication authentication) {
//        final User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
//        final String token = TokenUtils.generateJwtToken(user);
//        response.addHeader(AuthConstants.AUTH_HEADER, AuthConstants.TOKEN_TYPE + " " + token);
//    }

}
