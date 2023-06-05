package com.ssg.webpos.domain;

import com.ssg.webpos.domain.enums.NotificationType;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Notification extends BaseTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id")
  private Long id;

  private String title;
  private String content;
  private boolean isRead;

  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hq_admin_id")
  private HQAdmin hqAdmin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_admin_id")
  private BranchAdmin branchAdmin;

  public Notification(String title, String content, boolean isRead, NotificationType notificationType, HQAdmin hqAdmin, BranchAdmin branchAdmin) {
    this.title = title;
    this.content = content;
    this.isRead = isRead;
    this.notificationType = notificationType;
    this.hqAdmin = hqAdmin;
    this.branchAdmin = branchAdmin;
  }
}
