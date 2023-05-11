package com.ssg.webpos.service;

import com.ssg.webpos.domain.Delivery;
import com.ssg.webpos.dto.DeliveryDTO;
import com.ssg.webpos.repository.delivery.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DeliveryServiceTest {
	@Autowired
	DeliveryRepository deliveryRepository;
	@Autowired
	DeliveryService deliveryService;

	@Test
	void addDeliveryAddressTest() {
		DeliveryDTO deliveryDTO = new DeliveryDTO();
		deliveryDTO.setDeliveryName("우리집");
		deliveryDTO.setPhoneNumber("010-5555-2525");
		deliveryDTO.setUserName("김진아");
		deliveryDTO.setAddress("부산광역시 부산진구");
		deliveryService.addDeliveryAddress(deliveryDTO);

		List<Delivery> deliveryList = deliveryRepository.findAll();
		for(Delivery delivery: deliveryList) {
			System.out.println("delivery = " + delivery);
		}
//		assertEquals(1, deliveryList.size());
	}

	@Test
	void checkDeliveryListTest() {
		List<Delivery> deliveryList = deliveryRepository.findAll();
		for(Delivery delivery : deliveryList) {
			System.out.println("delivery = " + delivery);
		}
	}

	@Test
	void updateDeliveryInfoTest() {
		Long deliveryId = 1L;
		Delivery delivery = deliveryRepository.findById(1L).get();


	}

	@Test
	void deleteDeliveryInfoTest() {

	}

}
