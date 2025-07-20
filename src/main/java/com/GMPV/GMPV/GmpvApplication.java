package com.GMPV.GMPV;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = {"entity", "repository", "com.GMPV"})
public class GmpvApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmpvApplication.class, args);
	}

	 @Bean
	    public CommandLineRunner testEncoder(PasswordEncoder encoder) {
	        return args -> {
	            System.out.println("Hashed password for '123456': " + encoder.encode("123456"));
	        };
	    }

}
