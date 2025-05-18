package ureca.ureca_mini.user.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ureca.ureca_mini.user.jwt.JWTFilter;
import ureca.ureca_mini.user.jwt.JWTUtil;
import ureca.ureca_mini.user.jwt.LoginFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final UserDetailsService customUserDetailsService;

    public SecurityConfig(JWTUtil jwtUtil, UserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authManager) throws Exception {
        // 로그인 처리 필터 설정
        LoginFilter loginFilter = new LoginFilter(authManager, jwtUtil);
        loginFilter.setFilterProcessesUrl("/api/auth/login");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.GET,
                                "/", "/login", "/login/page", "/signup", "/callback"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/signup",
                                "/api/auth/**"
                        ).permitAll()

                        .requestMatchers("/favicon.ico").permitAll()

                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**", "/static/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                // JWT 검증 필터
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                // 로그인 처리 필터
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("http://localhost:8080"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}