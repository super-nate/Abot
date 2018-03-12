package online.abot.alertbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlertbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertbotApplication.class, args);
	}
}
