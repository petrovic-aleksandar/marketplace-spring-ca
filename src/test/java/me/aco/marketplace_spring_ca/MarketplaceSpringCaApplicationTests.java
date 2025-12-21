package me.aco.marketplace_spring_ca;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@SpringBootTest
class MarketplaceSpringCaApplicationTests {

	@Autowired
	private JpaUserRepository userRepository;

	@Test
	void contextLoads() {
		assert userRepository != null;
	}

	@Test
	void testFindAdminUser() {
		var adminUser = userRepository.findSingleByUsername("admin");
		assert adminUser.isPresent();
		assert adminUser.get().getRole().toString().equals("ADMIN");
	}

}
