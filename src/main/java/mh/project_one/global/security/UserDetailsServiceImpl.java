package mh.project_one.global.security;

import mh.project_one.global.security.principal.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service("userDetailsService") // 빈 이름을 명시적으로 지정할 수 있습니다.
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SecurityUserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(SecurityUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        user.updateLastLogin(LocalDateTime.now());

        return UserPrincipal.create(user);
    }
}