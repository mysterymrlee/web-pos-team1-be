package com.ssg.webpos.dto.notification;

import com.ssg.webpos.domain.HQAdmin;
import com.ssg.webpos.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
  private HQAdmin hqAdmin;
  private String title;
  private String content;
  private boolean isRead;
  private NotificationType notificationType;
}
