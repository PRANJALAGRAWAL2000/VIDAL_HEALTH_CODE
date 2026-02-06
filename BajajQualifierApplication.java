package com.bajaj.qualifier;

import com.bajaj.qualifier.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BajajQualifierApplication implements CommandLineRunner {

	@Autowired
	private ProcessService processService;

	public static void main(String[] args) {
		SpringApplication.run(BajajQualifierApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		processService.executeQualifierProcess();
	}
}
