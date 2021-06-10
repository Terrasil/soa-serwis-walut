package pl.gdynia.amw.Laboratorium6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableRetry
@EnableScheduling
@SpringBootApplication
public class Laboratorium6Application {

	public static void main(String[] args) {
		SpringApplication.run(Laboratorium6Application.class, args);
	}

}
