package ru.sber.sberlunch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SberlunchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SberlunchApplication.class, args);
	}

}
