package com.codeoftheweb.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repository) {
		return (args) -> {
			repository.save(new Player("Jack@mailcom"));
			repository.save(new Player("Chloe@mail.com"));
			repository.save(new Player("Michelle@mail.com"));
			repository.save(new Player("Dede@mail.com"));
			repository.save(new Player("Dudu@mail.com"));
		};
	}
}
