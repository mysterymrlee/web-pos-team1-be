package com.ssg.webpos.dto.notification;

import com.ssg.webpos.domain.HQAdmin;
import com.ssg.webpos.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
  private String content;
  private HQAdmin hqAdmin;
  private NotificationType notificationType;
}
