package com.nicodev.birdyapp;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class BirdyAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
