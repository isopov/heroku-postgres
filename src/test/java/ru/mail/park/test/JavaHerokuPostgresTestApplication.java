package ru.mail.park.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import ru.mail.park.JavaHerokuPostgresApplication;

@SpringBootApplication
@Import(JavaHerokuPostgresApplication.class)
public class JavaHerokuPostgresTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaHerokuPostgresTestApplication.class, args);
	}
}
