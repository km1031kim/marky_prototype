package com.groom.marky.advisor;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;

import com.groom.marky.common.TmapGeocodingClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Description("목적지 주소를 위경도로 변환하는 어드바이저")
public class LocationResolverAdvisor implements CallAdvisor {

	private final static String DEPARTURE_KEY = "departure";
	private final static String DESTINATION_KEY = "destination";
	private final TmapGeocodingClient tmapClient;

	@Autowired
	public LocationResolverAdvisor(TmapGeocodingClient tmapClient) {
		this.tmapClient = tmapClient;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {

		log.info("LocationResolverAdvisor 진입");

		String departure = (String)request.context().get(DEPARTURE_KEY);
		String destination = (String)request.context().get(DESTINATION_KEY);

		if (departure == null || departure.isBlank() || destination == null || destination.isBlank()) {
			log.warn("출발지/목적지 누락 → LocationResolverAdvisor 생략");
			return chain.nextCall(request);
		}

		Map<String, Double> departCoord = tmapClient.getLatLon(departure);
		Map<String, Double> destCoord = tmapClient.getLatLon(destination);

		if (departCoord == null || destCoord == null) {
			log.warn("tmapClient 결과가 null");
			return chain.nextCall(request);
		}

		SystemMessage systemMessage = new SystemMessage("출발지와 목적지를 받아서 위경도를 반환하고 다음 체인으로 넘겨줘");


		ChatClientRequest modified = ChatClientRequest.builder()
			.prompt(request.prompt())
			.context(request.context())
			.context("departureLat", departCoord.get("lat"))
			.context("departureLon", departCoord.get("lon"))
			.context("destinationLat", destCoord.get("lat"))
			.context("destinationLon", destCoord.get("lon"))
			.build();

		return chain.nextCall(modified);
	}

	@Override
	public String getName() {
		return "LocationResolverAdvisor";
	}

	@Override
	public int getOrder() {
		return 2;
	}
}
