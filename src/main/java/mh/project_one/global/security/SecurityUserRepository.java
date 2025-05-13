package mh.project_one.global.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {

    // 사용자 이름(로그인 ID)으로 사용자 정보 조회
    Optional<SecurityUser> findByUsername(String username);
}