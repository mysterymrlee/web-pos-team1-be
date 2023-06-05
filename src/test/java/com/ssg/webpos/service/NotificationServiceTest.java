package com.ssg.webpos.service;

import com.ssg.webpos.domain.HQAdmin;
import com.ssg.webpos.domain.Store;
import com.ssg.webpos.domain.enums.NotificationType;
import com.ssg.webpos.domain.enums.RoleHQadmin;
import com.ssg.webpos.dto.notification.NotificationDTO;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
class NotificationServiceTest {
	@Autowired
	NotificationService notificationService;

	@Test
	@DisplayName("알림 구독 테스트")
	void subscribe() {
		HQAdmin hqAdmin = new HQAdmin();
		hqAdmin.setAdminNumber("12345");
		hqAdmin.setName("관리자A");
		hqAdmin.setPassword("1234");
		hqAdmin.setRole(RoleHQadmin.ROLE_HQ);

		String lastEventId = "";

//		Assertions.assertDoesNotThrow(() -> notificationService.subscribe(hqAdmin.getId(), lastEventId));
	}

	@Test
	@DisplayName("알림 메시지 전송")
	public void send() {
		HQAdmin hqAdmin = new HQAdmin();
		hqAdmin.setAdminNumber("22222");
		hqAdmin.setName("관리자B");
		hqAdmin.setPassword("1234");
		hqAdmin.setRole(RoleHQadmin.ROLE_HQ);

		String lastEventId = "";
//		notificationService.subscribe(hqAdmin.getId(), lastEventId);
		NotificationDTO notificationDTO = NotificationDTO.builder()
				.hqAdmin(hqAdmin)
				.title("발주 신청 안내")
				.content("발주 신청이 완료되었습니다.")
				.isRead(false)
				.notificationType(NotificationType.ORDER_REQUEST)
				.build();
		System.out.println("notificationDTO = " + notificationDTO);
		Assertions.assertDoesNotThrow(() -> notificationService.send(notificationDTO));

	}
}
