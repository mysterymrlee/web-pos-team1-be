package com.ssg.webpos.controller;

import com.ssg.webpos.domain.BranchAdmin;
import com.ssg.webpos.dto.notification.NotificationDTO;
import com.ssg.webpos.repository.BranchAdminRepository;
import com.ssg.webpos.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
public class NotificationController {
  private final NotificationService notificationService;
  private final BranchAdminRepository branchAdminRepository;
  public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

  @GetMapping(value = "/subscribe/{hqId}", produces = "text/event-stream")
  public SseEmitter subscribe(@PathVariable Long hqId, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId, @RequestBody NotificationDTO notificationDTO) {
    return notificationService.subscribe(hqId, lastEventId, notificationDTO);
  }

  @PostMapping("/send/{hqId}")
  public ResponseEntity send(@PathVariable Long hqId, @RequestBody NotificationDTO notificationDTO) {
    notificationService.send(notificationDTO);
    return new ResponseEntity(notificationDTO, HttpStatus.OK);
  }

  @GetMapping("/ui")
  public String testUi() {
    return "notificationTest";
  }

  @GetMapping(value = "/sub/{hq}", produces = "text/event-stream")
  public SseEmitter connect(@PathVariable Long hq, NotificationDTO notificationDTO) {
    SseEmitter sseEmitter1 = notificationService.connect(hq);
    notificationService.sendNoti(hq);
    return sseEmitter1;
  }
}
