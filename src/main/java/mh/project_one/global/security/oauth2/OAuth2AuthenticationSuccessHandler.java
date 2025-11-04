package mh.project_one.global.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import mh.project_one.global.security.jwt.JwtTokenProvider;
import mh.project_one.global.security.principal.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 성공 시 JWT를 발급
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider, ObjectMapper objectMapper) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
        // 기본적으로 리다이렉트 대신 JSON 응답을 사용하도록 설정
        setRedirectStrategy((request, response, url) -> {
            // no-op
        });
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        UserPrincipal principal;
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            principal = userPrincipal;
        } else {
            throw new IllegalStateException("OAuth2 사용자 정보를 확인할 수 없습니다.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("tokenType", "Bearer");
        body.put("accessToken", tokenProvider.createAccessToken(principal));
        body.put("refreshToken", tokenProvider.createRefreshToken(principal));

        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}
