package mh.project_one.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiverUser; // 알림을 받을 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id") // nullable = true (시스템 알림 등 발신자 없는 경우)
    private User senderUser; // 알림을 발생시킨 사용자

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType; // 예: "NEW_ANSWER", "NEW_COMMENT"

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message; // 알림 내용

    @Column(name = "related_url", length = 255)
    private String relatedUrl; // 알림 클릭 시 이동할 URL

    @Column(name = "is_read", nullable = false)
    @ColumnDefault("false")
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(User receiverUser, User senderUser, String notificationType, String message, String relatedUrl, boolean isRead) {
        this.receiverUser = receiverUser;
        this.senderUser = senderUser; // 시스템 알림의 경우 null일 수 있음
        this.notificationType = notificationType;
        this.message = message;
        this.relatedUrl = relatedUrl;
        this.isRead = isRead;
    }

    // 연관관계 편의 메서드
    public void setReceiverUser(User receiverUser) {
        this.receiverUser = receiverUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    // 읽음 상태 변경 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}