package mh.project_one.domain.repository;

import mh.project_one.domain.entity.User;
import mh.project_one.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 특정 사용자가 특정 대상에 대해 한 투표 조회
    Optional<Vote> findByUserAndTargetTypeAndTargetId(User user, String targetType, Long targetId);

    // 특정 대상에 대한 모든 투표 기록 조회
    List<Vote> findByTargetTypeAndTargetId(String targetType, Long targetId);

    // 특정 대상의 총 투표 수(vote_type 합산) 계산 (JPQL 사용 예시)
    @Query("SELECT SUM(v.voteType) FROM Vote v WHERE v.targetType = :targetType AND v.targetId = :targetId")
    Optional<Integer> sumVoteTypeByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    // 특정 대상의 추천(voteType=1) 수 계산
    long countByTargetTypeAndTargetIdAndVoteType(String targetType, Long targetId, short voteType);
}