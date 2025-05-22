package com.groom.marky.api;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.groom.marky.domain.GooglePlaceType;
import com.groom.marky.domain.request.Rectangle;
import com.groom.marky.domain.response.GooglePlacesApiResponse;
import com.groom.marky.service.EmbeddingService;
import com.groom.marky.service.SeoulPlaceSearchService;
import com.groom.marky.service.impl.GooglePlaceSearchServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api")
public class GoogleMapController {

	private final GooglePlaceSearchServiceImpl googlePlaceSearchService;
	private final SeoulPlaceSearchService seoulPlaceSearchService;
	private final EmbeddingService embeddingService;

	@Autowired
	public GoogleMapController(GooglePlaceSearchServiceImpl googlePlaceSearchService,
		SeoulPlaceSearchService seoulPlaceSearchService, EmbeddingService embeddingService) {
		this.googlePlaceSearchService = googlePlaceSearchService;
		this.seoulPlaceSearchService = seoulPlaceSearchService;
		this.embeddingService = embeddingService;
	}

	@GetMapping("/google/search/text")
	public ResponseEntity<?> searchText() {

		// kakao cafe 57
	/*	Rectangle box = new Rectangle(
			127.0016985,
			37.684949100000004,
			127.055221,
			37.715133);*/

		 Set<Rectangle> parkingLotRects = seoulPlaceSearchService.getParkingLotRects();

		log.info("박스 수 : {} " , parkingLotRects.size());

		for (Rectangle rect : parkingLotRects) {
			GooglePlacesApiResponse response = googlePlaceSearchService.search("주차장", GooglePlaceType.PARKING, rect);
			log.info("rect : {}, 장소 수 : {} ", rect, response.places().size());
			embeddingService.saveEmbeddings(response);
			log.info("임베딩 완료");
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/google/search/nearby")
	public ResponseEntity<?> searchNearby() {

		// 126.91844127777779,37.550798433333334,126.92141475000001,37.552475316666666, total : 42

		// kakao cafe 50
		Rectangle box = new Rectangle(
			127.0016985,
			37.684949100000004,
			127.055221,
			37.715133); // 50

		// 응답
		GooglePlacesApiResponse response = googlePlaceSearchService.searchNearby(List.of("restaurant"), box);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
