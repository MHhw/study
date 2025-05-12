package mh.project_one.domain.repository;

import mh.project_one.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // 태그 이름으로 태그 조회
    Optional<Tag> findByName(String name);

    // 여러 태그 이름으로 태그 목록 조회
    List<Tag> findByNameIn(Set<String> names);

    // 태그 이름 존재 여부 확인
    boolean existsByName(String name);
}