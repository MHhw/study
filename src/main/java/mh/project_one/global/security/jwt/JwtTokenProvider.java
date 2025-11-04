package mh.project_one.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import mh.project_one.global.security.principal.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT 생성, 검증, 파싱 로직
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties properties;
    private final UserDetailsService userDetailsService;
    private final Key signingKey;

    public JwtTokenProvider(JwtProperties properties, UserDetailsService userDetailsService) {
        this.properties = properties;
        this.userDetailsService = userDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 액세스 토큰 생성
     */
    public String createAccessToken(UserPrincipal principal) {
        return createToken(principal, properties.getAccessTokenValidityInSeconds());
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(UserPrincipal principal) {
        return createToken(principal, properties.getRefreshTokenValidityInSeconds());
    }

    private String createToken(UserPrincipal principal, long validityInSeconds) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(validityInSeconds);
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
        List<String> roles = CollectionUtils.isEmpty(authorities)
                ? List.of()
                : authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .addClaims(Map.of(
                        "uid", principal.getId(),
                        "email", principal.getEmail(),
                        "roles", roles
                ))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰", e);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰", e);
        }
        return false;
    }

    /**
     * 토큰으로 Authentication 객체 생성
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String username = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    private Claims parseClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
        return claimsJws.getBody();
    }
}
