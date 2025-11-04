package mh.project_one.global.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * JWT 관련 설정 값 관리
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Base64 인코딩된 서명 비밀키
     */
    private String secret;

    /**
     * 액세스 토큰 유효 시간 (초)
     */
    private long accessTokenValidityInSeconds = Duration.ofHours(1).toSeconds();

    /**
     * 리프레시 토큰 유효 시간 (초)
     */
    private long refreshTokenValidityInSeconds = Duration.ofDays(14).toSeconds();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        Assert.hasText(secret, "JWT 비밀키는 필수입니다.");
        this.secret = secret;
    }

    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidityInSeconds;
    }

    public void setAccessTokenValidityInSeconds(long accessTokenValidityInSeconds) {
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
    }

    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInSeconds;
    }

    public void setRefreshTokenValidityInSeconds(long refreshTokenValidityInSeconds) {
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }
}
