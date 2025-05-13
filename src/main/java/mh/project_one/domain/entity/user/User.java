package mh.project_one.domain.entity.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// @Entity
// @Table(name = "users") // 테이블명 명시 (클래스명과 다를 경우)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자를 필요로 함 (protected 접근 수준 권장)
public class User {

}