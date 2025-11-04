package mh.project_one.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import mh.project_one.global.security.jwt.JwtTokenProvider;
import mh.project_one.global.security.principal.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 폼 로그인 성공 시 JWT를 발급하여 반환
 */
@Component
public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public AuthenticationSuccessHandler(JwtTokenProvider tokenProvider, ObjectMapper objectMapper) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        UserPrincipal principal;
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            principal = userPrincipal;
        } else {
            throw new IllegalStateException("JWT 발급을 위한 사용자 정보를 찾을 수 없습니다.");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("tokenType", "Bearer");
        result.put("accessToken", tokenProvider.createAccessToken(principal));
        result.put("refreshToken", tokenProvider.createRefreshToken(principal));
        result.put("redirectUrl", "/questions/view");

        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }
}