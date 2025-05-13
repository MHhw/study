package mh.project_one.domain.repository.user;

import mh.project_one.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // User 엔티티를 다루고, User의 ID 타입은 Long 임을 명시
    // 필요한 경우 여기에 커스텀 쿼리 메소드를 추가할 수 있습니다.
    // 예: Optional<User> findByUsername(String username);
}