package com.example.musicsubscription;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MusicSubscriptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicSubscriptionApplication.class, args);

	}
	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return servletContext -> servletContext.setInitParameter("server.ssl.enabled", "true");
	}

	@Bean
	public CommandLineRunner initializeData(MusicTableController musicTableController) {
		return args -> {
			try {
				musicTableController.createMusicTable();
			} catch (Exception e) {
				System.err.println("Failed to initialize data: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}

}
