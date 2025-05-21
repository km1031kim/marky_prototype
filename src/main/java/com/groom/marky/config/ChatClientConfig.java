package com.groom.marky.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.groom.marky.advisor.DestinationExtractionAdvisor;
import com.groom.marky.advisor.LocationResolverAdvisor;
import com.groom.marky.advisor.SubwayRouteAdvisor;
import com.groom.marky.advisor.SystemRoleAdvisor;
import com.groom.marky.common.TmapGeocodingClient;
import com.groom.marky.common.TmapTransitClient;
@Configuration
public class ChatClientConfig {

	@Bean
	public ChatClient ollamaChatClient(
		OllamaChatModel model,
		SystemRoleAdvisor systemRoleAdvisor,
		DestinationExtractionAdvisor destinationExtractionAdvisor,
		LocationResolverAdvisor locationResolverAdvisor,
		SubwayRouteAdvisor subwayRouteAdvisor
	) {
		return ChatClient.builder(model)
			.defaultAdvisors(
				systemRoleAdvisor,
				destinationExtractionAdvisor,
				locationResolverAdvisor,
				subwayRouteAdvisor
			)
			.build();
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		return restTemplate;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}

	@Bean
	public DestinationExtractionAdvisor destinationExtractionAdvisor(
		ChatModel chatModel,
		ObjectMapper objectMapper
	) {
		return new DestinationExtractionAdvisor(chatModel, objectMapper);
	}

	@Bean
	public SystemRoleAdvisor systemRoleAdvisor() {
		return new SystemRoleAdvisor();
	}

	@Bean
	public TmapGeocodingClient tmapGeocodingClient(RestTemplate restTemplate) {
		return new TmapGeocodingClient(restTemplate);
	}

	@Bean
	public LocationResolverAdvisor locationResolverAdvisor(TmapGeocodingClient tmapGeocodingClient) {
		return new LocationResolverAdvisor(tmapGeocodingClient);
	}

	@Bean
	public TmapTransitClient tmapTransitClient(ObjectMapper objectMapper, RestTemplate restTemplate) {
		return new TmapTransitClient(objectMapper, restTemplate);
	}

	@Bean
	public SubwayRouteAdvisor subwayRouteAdvisor(TmapTransitClient tmapTransitClient) {
		return new SubwayRouteAdvisor(tmapTransitClient);
	}

}
