package ureca.ureca_mini.user.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ureca.ureca_mini.user.dto.CustomUserDetails;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        try {
            // JSON 바디를 LoginRequest로 파싱
            ObjectMapper mapper = new ObjectMapper();
            LoginRequest creds = mapper.readValue(request.getInputStream(), LoginRequest.class);

            // UsernamePasswordAuthenticationToken 생성하여 인증 시도
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword()
                    );
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse authentication request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        // 1) JWT 생성 (예: 1시간)
        String token = jwtUtil.createJwt(userDetails.getUsername(), 1000L * 60 * 60);

        // 2) 응답 헤더에만 토큰 담기
        response.setHeader("Authorization", "Bearer " + token);
        response.setStatus(HttpServletResponse.SC_OK);
    }



    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.println("<script>alert('이메일 또는 비밀번호가 올바르지 않습니다.'); location.href='/login';</script>");
        writer.flush();
    }


    /**
     * 내부 DTO: Jackson이 바디를 바인딩할 수 있도록 getter/setter가 있는 형태로 정의
     */
    @Data
    private static class LoginRequest {
        private String email;
        private String password;
    }
}
