package com.groom.marky.service.impl;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.groom.marky.domain.request.Circle;
import com.groom.marky.domain.request.LocationRestriction;
import com.groom.marky.domain.request.PlacesNearbyRequest;
import com.groom.marky.domain.request.PlacesTextRequest;
import com.groom.marky.domain.request.Rectangle;
import com.groom.marky.domain.response.GooglePlacesApiResponse;
import com.groom.marky.domain.response.PlaceDescriptionBuilder;
import com.groom.marky.service.GooglePlaceSearchService;

@Service
public class GooglePlaceSearchServiceImpl implements GooglePlaceSearchService {

	private static final String LANGUAGE_CODE = "ko";
	private static final String REGION_CODE = "KR";
	private static final String GOOGLE_API_BASE = "https://places.googleapis.com";
	private static final String SEARCH_PATH = "/v1/places:searchText";
	private static final String SEARCH_NEARBY_PATH = "/v1/places:searchNearby";

	private static final String TEXT_FIELD_HEADER = String.join(",",
		"places.id",
		"places.displayName",
		"places.formattedAddress",
		"places.location",
		"places.reviews",
		"places.types",
		"places.rating",
		"places.userRatingCount",
		"nextPageToken",
		"places.primaryTypeDisplayName",
		"places.regularOpeningHours",
		"places.allowsDogs",
		"places.curbsidePickup",
		"places.delivery",
		"places.dineIn",
		"places.goodForChildren",
		"places.goodForGroups",
		"places.goodForWatchingSports",
		"places.liveMusic",
		"places.menuForChildren",
		"places.parkingOptions",
		"places.paymentOptions.acceptsCreditCards",
		"places.paymentOptions.acceptsDebitCards",
		"places.paymentOptions.acceptsCashOnly",
		"places.outdoorSeating",
		"places.reservable",
		"places.restroom",
		"places.servesBeer",
		"places.servesBreakfast",
		"places.servesBrunch",
		"places.servesCocktails",
		"places.servesCoffee",
		"places.servesDessert",
		"places.servesDinner",
		"places.servesLunch",
		"places.servesVegetarianFood",
		"places.servesWine",
		"places.takeout"
	);


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
	public List<String> search(String text, Rectangle rect) {

		ArrayList<GooglePlacesApiResponse.Place> places = new ArrayList<>();

		String nextPageToken = null;

		PlacesTextRequest request = buildRequest(text, rect, nextPageToken);

		// 헤더 세팅
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Goog-FieldMask", TEXT_FIELD_HEADER);
		headers.set("X-Goog-Api-Key", apiKey);

		// 요청 생성
		HttpEntity<PlacesTextRequest> httpEntity = new HttpEntity<>(request, headers);

		GooglePlacesApiResponse response = restTemplate.exchange(getGoogleSearchTextUri(), HttpMethod.POST, httpEntity,
			GooglePlacesApiResponse.class).getBody();

		// 응답 담기
		if (response != null && response.places() != null) {
			places.addAll(response.places());
			nextPageToken = response.nextPageToken();
		}

		while (nextPageToken != null) {
			request = buildRequest(text, rect, nextPageToken);
			httpEntity = new HttpEntity<>(request, headers);
			response = restTemplate.exchange(getGoogleSearchTextUri(), HttpMethod.POST, httpEntity,
				GooglePlacesApiResponse.class).getBody();

			// 응답 담기
			if (response != null && response.places() != null) {
				places.addAll(response.places());
				nextPageToken = response.nextPageToken();
			}
		}

		return places.stream()
			.map(PlaceDescriptionBuilder::buildDescription)
			.collect(Collectors.toCollection(ArrayList::new));
	}

	private static PlacesTextRequest buildRequest(String text, LocationRestriction restriction, String pageToken) {
		return PlacesTextRequest.builder()
			.pageToken(pageToken)
			.textQuery(text)
			.languageCode(LANGUAGE_CODE)
			.regionCode(REGION_CODE)
			.locationRestriction(restriction)
			.build();
	}

	@Override
	public GooglePlacesApiResponse searchNearby(List<String> types, Rectangle rect) {

		// 바디
		PlacesNearbyRequest request = PlacesNearbyRequest.builder()
			.includedTypes(types)
			.languageCode(LANGUAGE_CODE)
			.regionCode(REGION_CODE)
			.locationRestriction(Circle.from(rect))
			.build();

		// 헤더 세팅
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// TODO : 추가 필요.
		headers.set("X-Goog-FieldMask",
			"places.id,places.displayName,places.formattedAddress,places.location,places.types");
		headers.set("X-Goog-Api-Key", apiKey);

		// 요청 생성
		HttpEntity<PlacesNearbyRequest> httpEntity = new HttpEntity<>(request, headers);

		return restTemplate.exchange(getGoogleSearchNearByUri(), HttpMethod.POST, httpEntity,
			GooglePlacesApiResponse.class).getBody();
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
