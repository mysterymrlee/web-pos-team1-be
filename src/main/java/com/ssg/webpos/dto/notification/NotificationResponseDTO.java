package com.ssg.webpos.dto.notification;

import com.ssg.webpos.domain.Notification;
import com.ssg.webpos.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {
  private Long id;
  private String title;
  private String content;

  public static NotificationResponseDTO create(Notification notification) {
    NotificationResponseDTO dto = new NotificationResponseDTO();
    dto.setId(notification.getId());
    dto.setTitle(notification.getTitle());
    dto.setContent(notification.getContent());

    return dto;
  }
}
