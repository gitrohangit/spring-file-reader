package com.springboot.file.springfiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.springboot.file.springfiles"})
public class SpringfilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringfilesApplication.class, args);
	}

}
