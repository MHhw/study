package mh.project_one.global.security.oauth2;

import mh.project_one.domain.entity.user.User;
import mh.project_one.domain.service.user.UserService;
import mh.project_one.global.security.principal.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 제공자로부터 사용자 정보를 가져와 내부 사용자 정보로 매핑
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = registrationId.toUpperCase(Locale.ROOT);
        String providerId = resolveProviderId(provider, attributes);
        String email = resolveEmail(provider, attributes);
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("OAuth2 공급자가 이메일을 제공하지 않았습니다.");
        }
        String name = resolveName(provider, attributes);
        String imageUrl = resolveImageUrl(provider, attributes);

        Optional<User> optionalUser = userService.findByProviderAndProviderId(provider, providerId);
        User user = optionalUser
                .or(() -> userService.findByEmail(email))
                .orElseGet(() -> registerNewUser(provider, providerId, email, name, imageUrl));

        User synchronizedUser = userService.synchronizeSocialUser(user, provider, providerId, email, name, imageUrl);

        return UserPrincipal.create(synchronizedUser, attributes);
    }

    private User registerNewUser(String provider, String providerId, String email, String name, String imageUrl) {
        log.info("신규 OAuth2 사용자 등록: provider={}, providerId={}", provider, providerId);
        String username = email;
        if (!StringUtils.hasText(username)) {
            username = provider.toLowerCase(Locale.ROOT) + "_" + providerId;
        }

        String nickname = StringUtils.hasText(name) ? name : username;

        return userService.registerSocialUser(provider, providerId, email, nickname, imageUrl);
    }

    private String resolveProviderId(String provider, Map<String, Object> attributes) {
        if ("GOOGLE".equals(provider)) {
            return String.valueOf(attributes.get("sub"));
        }
        if ("GITHUB".equals(provider)) {
            return String.valueOf(attributes.get("id"));
        }
        return String.valueOf(attributes.getOrDefault("id", ""));
    }

    private String resolveEmail(String provider, Map<String, Object> attributes) {
        Object email = attributes.get("email");
        if (email != null) {
            return String.valueOf(email);
        }
        if ("GITHUB".equals(provider)) {
            Object login = attributes.get("login");
            if (login != null) {
                return login + "@github.com";
            }
        }
        return null;
    }

    private String resolveName(String provider, Map<String, Object> attributes) {
        Object name = attributes.get("name");
        if (name != null) {
            return String.valueOf(name);
        }
        if ("GOOGLE".equals(provider)) {
            Object givenName = attributes.get("given_name");
            if (givenName != null) {
                return String.valueOf(givenName);
            }
        }
        return null;
    }

    private String resolveImageUrl(String provider, Map<String, Object> attributes) {
        Object picture = attributes.get("picture");
        if (picture != null) {
            return String.valueOf(picture);
        }
        if ("GITHUB".equals(provider)) {
            Object avatar = attributes.get("avatar_url");
            if (avatar != null) {
                return String.valueOf(avatar);
            }
        }
        return null;
    }
}
