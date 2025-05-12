package mh.project_one.domain.repository;

import mh.project_one.domain.entity.Notification;
import mh.project_one.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 수신자의 알림 목록 페이징 조회 (최신순)
    Page<Notification> findByReceiverUserOrderByCreatedAtDesc(User receiverUser, Pageable pageable);

    // 특정 수신자의 읽지 않은 알림 수 조회
    long countByReceiverUserAndIsReadFalse(User receiverUser);

    // 특정 수신자의 특정 알림 ID 목록에 대해 읽음 처리 (JPQL 사용 예시)
    @Modifying // SELECT가 아닌 UPDATE, DELETE 쿼리 실행 시 필요
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiverUser = :receiverUser AND n.notificationId IN :notificationIds")
    int markAsReadByIds(@Param("receiverUser") User receiverUser, @Param("notificationIds") List<Long> notificationIds);

    // 특정 수신자의 모든 알림 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiverUser = :receiverUser AND n.isRead = false")
    int markAllAsReadByUser(@Param("receiverUser") User receiverUser);
}