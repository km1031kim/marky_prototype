package com.groom.marky.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${GOOGLE_API_KEY}")
	private String googleApiKey;

	@Bean
	public WebClient googleClient() {
		return WebClient.builder()
			.baseUrl("https://places.googleapis.com")
			.defaultHeader("X-Goog-Api-Key", googleApiKey)
			.build();
	}

}
