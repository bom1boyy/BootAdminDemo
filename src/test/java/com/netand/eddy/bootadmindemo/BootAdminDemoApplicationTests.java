package com.netand.eddy.bootadmindemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BootAdminDemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void 테스트용(){
		int a = 1;
		int b = 2;
		assertEquals( 3, a+b );
	}

}
