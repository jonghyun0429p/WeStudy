package com.westudy.security.config;

import com.westudy.security.filter.CustomUsernamePasswordFilter;
import com.westudy.security.filter.JwtAuthenticationFilter;
import com.westudy.security.handler.CustomAuthenticationSuccessHandler;
import com.westudy.security.provider.JwtTokenProvider;
import com.westudy.security.service.CustomUserDetailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(customUserDetailService)
//                .passwordEncoder(passwordEncoder());
//
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        CustomUsernamePasswordFilter customUsernamePasswordFilter =
                new CustomUsernamePasswordFilter(authenticationManager);
        customUsernamePasswordFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);

        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable()) // 일단 테스트과정에서는 사용 X
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //JWT사용하니까 세션 사용 안함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // 페이지 기본 구성
                        .requestMatchers("/login").permitAll()//페이지 로딩용
                        .requestMatchers("/api/auth/**", "/", "/users/signup", "/users/login").permitAll()// api 서버 관리용
                        .requestMatchers("/admin","/admin/**").hasRole("ADMIN")//관리자는 관리자만 페이지 이동 가능.
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })
                )
                .logout(logout-> logout.logoutUrl("/users/logout")
                        .logoutSuccessUrl("/"))
                // ✅ JWTAuthenticationFilter 는 가장 먼저 실행
                .addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class)
                // ✅ CustomUsernamePasswordFilter 는 UsernamePasswordAuthenticationFilter 앞에 (로그인 전용)
                .addFilterBefore(customUsernamePasswordFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
