package mh.project_one.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import mh.project_one.global.common.response.ApiResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 폼 로그인 실패 응답 처리
 */
@Component
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    public AuthenticationFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> payload = Map.of(
                "error", exception.getMessage()
        );

        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.failure("아이디 또는 비밀번호가 올바르지 않습니다.", payload)
        ));
        response.getWriter().flush();
    }
}