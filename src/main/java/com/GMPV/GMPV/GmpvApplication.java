package com.GMPV.GMPV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"entity", "repository", "com.GMPV"})
public class GmpvApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmpvApplication.class, args);
	}

	

}
