package com.totalelectro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TotalelectroApplication implements CommandLineRunner {

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(TotalelectroApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String s = passwordEncoder.encode("pass1234");
		System.out.println(s);
	}
}
