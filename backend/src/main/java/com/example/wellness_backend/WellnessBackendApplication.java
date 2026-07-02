package com.example.wellness_backend;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WellnessBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellnessBackendApplication.class, args);
	}
	@Bean
	public ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build();
	}
}
