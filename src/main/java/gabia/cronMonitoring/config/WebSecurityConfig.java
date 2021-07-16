package gabia.cronMonitoring.config;

import gabia.cronMonitoring.util.jwt.JwtAccessDeniedHandler;
import gabia.cronMonitoring.util.jwt.JwtAuthenticationEntryPoint;
import gabia.cronMonitoring.util.jwt.JwtSecurityConfig;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(
                "/h2-console/**"
                , "/favicon.ico"
                , "/error"
            );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors().configurationSource(corsConfigurationSource())
            .and()

            // token을 사용하는 방식이기 때문에 csrf를 disable
            .csrf().disable()

            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)

            // enable h2-console
            .and()
            .headers()
            .frameOptions()
            .sameOrigin()

            // 세션을 사용하지 않기 때문에 STATELESS로 설정
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS,"/").permitAll()
            .antMatchers("/hello").permitAll()
            .antMatchers("/notifications/notice").permitAll()
            .antMatchers("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process").permitAll()
            .antMatchers("/cron-servers/{serverIp}/cron-jobs/{cronJobId}/process/{pid}").permitAll()
            .antMatchers("/auth/register").anonymous()
            .antMatchers("/auth/local/login").anonymous()

            .anyRequest().authenticated()

            .and()
            .apply(new JwtSecurityConfig(tokenProvider));
    }

    // CORS 허용 적용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
//        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
