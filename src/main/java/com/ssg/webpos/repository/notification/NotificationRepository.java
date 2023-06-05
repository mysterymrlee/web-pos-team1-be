package com.ssg.webpos.repository.notification;

import com.ssg.webpos.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
