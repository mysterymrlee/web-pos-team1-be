package com.ssg.webpos;

import com.ssg.webpos.domain.Store;
import com.ssg.webpos.repository.store.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class WebposApplicationTests {

	@Autowired
	StoreRepository storeRepository;

	@Test
	@DisplayName("Mock Store 데이터 삽입")
	@Rollback(value = false)
	void createStoreMockData() {
		Store newStore = Store.builder()
				.branchName("센텀점")
				.address("부산 해운대")
				.name("신세계 백화점")
				.postCode("1234") // java: incompatible types: int cannot be converted to java.lang.String 에러로 수정했습니다.
				.build();
		storeRepository.save(newStore);
	}
}
