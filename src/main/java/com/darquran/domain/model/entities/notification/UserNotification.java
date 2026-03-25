package com.darquran.domain.model.entities.notification;

import com.darquran.domain.model.entities.users.User;
import com.darquran.domain.model.enums.notification.UserNotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private UserNotificationType type;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 4000)
    private String body;

    @Column(length = 2000)
    private String linkUrl;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
