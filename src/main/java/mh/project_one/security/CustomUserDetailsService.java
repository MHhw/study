package mh.project_one.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SecurityRepository securityRepository;

    @Autowired
    public CustomUserDetailsService(SecurityRepository securityRepository){
        this.securityRepository = securityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return securityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다"));
    }

}
