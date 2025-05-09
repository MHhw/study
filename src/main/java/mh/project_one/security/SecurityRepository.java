package mh.project_one.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityRepository extends JpaRepository<CustomUser, Long> {

    // 사용자에게 입력받은 아이디로 사용자 정보 조회
    Optional<CustomUser> findByUsername(String username);
}
