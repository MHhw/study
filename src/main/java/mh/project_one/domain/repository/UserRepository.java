package mh.project_one.domain.repository;

import mh.project_one.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자명(로그인 ID)으로 사용자 조회
    Optional<User> findByUsername(String username);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 닉네임으로 사용자 조회
    Optional<User> findByNickname(String nickname);

    // 소셜 로그인 시 provider와 providerId로 사용자 조회
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    // 사용자명 존재 여부 확인
    boolean existsByUsername(String username);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);

    // 닉네임 존재 여부 확인
    boolean existsByNickname(String nickname);
}