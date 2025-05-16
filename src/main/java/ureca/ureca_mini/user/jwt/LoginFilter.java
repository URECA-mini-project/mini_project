// LoginFilter.java
package ureca.ureca_mini.user.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @PostConstruct
    public void init() {
        // form 로그인 시 사용할 파라미터 이름(email, password) 지정
        setUsernameParameter("email");
        setPasswordParameter("password");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

        String ct = request.getContentType();

        // 1) JSON 바디로 요청됐으면 직접 파싱
        if (ct != null && ct.contains(MediaType.APPLICATION_JSON_VALUE)) {
            try {
                LoginRequest creds = new ObjectMapper()
                        .readValue(request.getInputStream(), LoginRequest.class);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                creds.getEmail(), creds.getPassword()
                        );
                setDetails(request, authToken);
                return authenticationManager.authenticate(authToken);
            } catch (IOException e) {
                throw new AuthenticationServiceException("JSON 파싱 실패", e);
            }
        }

        // 2) 그 외 (form-data, x-www-form-urlencoded)는 부모 로직 사용
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        // 인증 성공 시 사용자 이메일(authResult.getName())로 JWT 생성 (1시간 유효)
        String token = jwtUtil.createJwt(authResult.getName(), 60 * 60 * 1000L);

        // JSON 형태로 토큰 반환
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"token\":\"" + token + "\"}");
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        // 인증 실패 시 401 + JSON 에러 메시지
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"Authentication Failed\"}");
    }

    // JSON 바디 바인딩용 DTO
    @Data
    private static class LoginRequest {
        private String email;
        private String password;
    }
}
