package mh.project_one.global.security.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 액세스/리프레시 토큰 발급 결과를 프론트엔드에 전달하기 위한 DTO 입니다.
 */
@Getter
@Builder
public class TokenResponse {
    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;
    private final String redirectUrl;
}
