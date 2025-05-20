package com.groom.marky.service.impl;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groom.marky.common.BoundingBox;
import com.groom.marky.common.BoundingCircle;
import com.groom.marky.domain.GooglePlacesResponse;
import com.groom.marky.domain.PlacesRequest;
import com.groom.marky.domain.google.request.Coordinate;
import com.groom.marky.domain.google.request.LocationRestriction;
import com.groom.marky.service.GooglePlaceSearchService;

@Service
public class GooglePlaceSearchServiceImpl implements GooglePlaceSearchService {

	private static final String LANGUAGE_CODE = "ko";
	private static final String REGION_CODE = "KR";
	private static final String GOOGLE_API_BASE = "https://places.googleapis.com";
	private static final String SEARCH_PATH = "/v1/places:searchText";
	private static final String SEARCH_NEARBY_PATH = "/v1/places:searchNearby";


	private String apiKey;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Autowired
	public GooglePlaceSearchServiceImpl(RestTemplate restTemplate,
		ObjectMapper objectMapper,
		@Value("${GOOGLE_API_KEY}") String apiKey) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
		this.apiKey = apiKey;
	}

	@Override
	public GooglePlacesResponse search(String text, BoundingBox box) {

		// 바디
		LocationRestriction locationRestriction = Coordinate.from(box);

		PlacesRequest request = PlacesRequest.builder()
			.textQuery(text)
			.languageCode(LANGUAGE_CODE)
			.regionCode(REGION_CODE)
			.locationRestriction(locationRestriction)
			.build();


		// 헤더 세팅
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Goog-FieldMask",
			"places.id,places.displayName,places.formattedAddress,places.location,"
				+ "places.reviews.text,places.types,places.rating,places.userRatingCount,nextPageToken");
		headers.set("X-Goog-Api-Key", apiKey);

		// 요청 생성
		HttpEntity<PlacesRequest> httpEntity = new HttpEntity<>(request, headers);

		return restTemplate.exchange(getGoogleSearchTextUri(), HttpMethod.POST, httpEntity,
			GooglePlacesResponse.class).getBody();
	}

	@Override
	public GooglePlacesResponse searchNearby(String text, BoundingBox box) {

		// 바디
		BoundingCircle circle = BoundingCircle.from(box);

		PlacesRequest request = PlacesRequest.builder()
			.textQuery(text)
			.languageCode(LANGUAGE_CODE)
			.regionCode(REGION_CODE)
			.locationRestriction()
			.build();


		// 헤더 세팅
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Goog-FieldMask",
			"places.id,places.displayName,places.formattedAddress,places.location,"
				+ "places.reviews.text,places.types,places.rating,places.userRatingCount,nextPageToken");
		headers.set("X-Goog-Api-Key", apiKey);

		// 요청 생성
		HttpEntity<PlacesRequest> httpEntity = new HttpEntity<>(request, headers);

		return restTemplate.exchange(getGoogleSearchNearByUri(), HttpMethod.POST, httpEntity,
			GooglePlacesResponse.class).getBody();
	}

	private URI getGoogleSearchTextUri() {
		return UriComponentsBuilder.fromUriString(GOOGLE_API_BASE + SEARCH_PATH)
			.encode(StandardCharsets.UTF_8)
			.build().toUri();
	}


	private URI getGoogleSearchNearByUri() {
		return UriComponentsBuilder.fromUriString(GOOGLE_API_BASE + SEARCH_NEARBY_PATH)
			.encode(StandardCharsets.UTF_8)
			.build().toUri();
	}
}
