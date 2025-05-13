package mh.project_one.domain.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime; // PostgreSQL의 TIMESTAMP WITH TIME ZONE과 매핑됩니다.
// 필요에 따라 ZonedDateTime 또는 OffsetDateTime 사용도 고려할 수 있습니다.

@Getter
@MappedSuperclass // 이 클래스는 테이블과 직접 매핑되지 않고, 자식 엔티티에게 필드만 상속합니다.
@EntityListeners(AuditingEntityListener.class) // 엔티티의 변경 이력을 감지하여 Auditing 정보를 자동으로 관리합니다.
public abstract class BaseTimeEntity { // 직접 인스턴스화될 필요가 없으므로 추상 클래스로 선언합니다.

    @CreatedDate // 엔티티가 생성되어 저장될 때 현재 시간이 자동 저장됩니다.
    @Column(name = "created_at", nullable = false, updatable = false) // DB 컬럼은 NOT NULL이어야 하며, 생성 시간은 업데이트되지 않도록 설정합니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티의 값이 변경될 때 현재 시간이 자동 저장됩니다.
    @Column(name = "updated_at", nullable = false) // DB 컬럼은 NOT NULL이어야 합니다.
    private LocalDateTime updatedAt;
}