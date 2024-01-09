package com.rawchen.javarun;

import com.rawchen.javarun.modules.monitor.MonitorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JavaRunApplicationTests {

	@Autowired
	MonitorService monitorService;

	@Test
	void contextLoads() {
	}

	@Test
	void compileCode() {
	}

	@Test
	void test01() {
		monitorService.websiteAvailable();
	}

}
