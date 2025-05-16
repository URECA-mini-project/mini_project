package ureca.ureca_mini.user.service;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ureca.ureca_mini.user.dto.KakaoTokenResponseDto;
import ureca.ureca_mini.user.dto.KakaoUserInfoResponseDto;
import ureca.ureca_mini.user.entity.UserEntity;
import ureca.ureca_mini.user.jwt.JWTUtil;
import ureca.ureca_mini.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.client_secret}")
    private String clientSecret;

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    private static final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
    private static final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

    public String getAccessTokenFromKakao(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        log.info("🔥 Kakao 요청 파라미터 확인");
        log.info("client_id = {}", clientId);
        log.info("redirect_uri = {}", redirectUri);
        log.info("code = {}", code);

        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create()
                .post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, res -> Mono.error(new RuntimeException("Server Error")))
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        log.info("access_token = {}", kakaoTokenResponseDto.getAccessToken());
        return kakaoTokenResponseDto.getAccessToken();
    }

    public String generateRefreshTokenFor(String username) {
        // Refresh token 유효 시간: 30일
        return jwtUtil.createRefreshToken(username, 30L * 24 * 60 * 60 * 1000);
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {

        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] NickName ---> {} ", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }

    public void processKakaoLogin(KakaoUserInfoResponseDto userInfo) {
        String email = userInfo.getKakaoAccount().getEmail();
        String username = "kakao_" + userInfo.getId();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 동일한 이메일로 가입된 로컬 계정이 존재합니다.");
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .email(userInfo.getKakaoAccount().getEmail())
                .nickname(userInfo.getKakaoAccount().getProfile().getNickName())
                .password("")
                .kakaoId(userInfo.getId())
                .createdAt(LocalDateTime.now())
                .provider("kakao")
                .build();
        userRepository.save(user);
    }

    public String generateJwtFor(Long kakaoId) {
        String username = "kakao_" + kakaoId;
        return jwtUtil.createJwt(username, 10L * 60 * 60 * 1000);
    }

    public boolean isEmailAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }
}