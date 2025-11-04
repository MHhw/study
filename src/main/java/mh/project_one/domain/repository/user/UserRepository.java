package mh.project_one.domain.repository.user;

import mh.project_one.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // 아이디 기반 조회

    Optional<User> findByEmail(String email); // 이메일 중복 여부 확인에 사용

    Optional<User> findByProviderAndProviderId(String provider, String providerId); // 소셜 로그인 계정 매핑
}