package mh.project_one.global.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("userDetailsService") // 빈 이름을 명시적으로 지정할 수 있습니다.
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SecurityUserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(SecurityUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true) // 지연 로딩 관련 문제를 피하기 위해 readOnly 트랜잭션 적용
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // User 엔티티가 UserDetails를 구현하므로 바로 반환 가능
        // 만약 User 엔티티가 UserDetails를 구현하지 않는다면, 여기서 User 정보를 바탕으로
        // org.springframework.security.core.userdetails.User 객체를 생성하여 반환해야 합니다.
        // 예: return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
        return user;
    }
}