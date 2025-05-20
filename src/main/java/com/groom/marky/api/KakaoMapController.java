package com.groom.marky.api;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.groom.marky.common.BoundingBox;
import com.groom.marky.service.SeoulPlaceSearchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api")
public class KakaoMapController {

	private final SeoulPlaceSearchService seoulPlaceSearchService;

	@Autowired
	public KakaoMapController(SeoulPlaceSearchService seoulPlaceSearchService) {
		this.seoulPlaceSearchService = seoulPlaceSearchService;
	}

	@GetMapping("/kakao")
	public ResponseEntity<?> callKeywordSearchApi() {
		// 박스 받아서 박스 범위 내의 주차장 조회 - 2000건
		Set<BoundingBox> cafeBoxes = seoulPlaceSearchService.getCafeBoxes();
		Set<BoundingBox> restaurantBoxes = seoulPlaceSearchService.getRestaurantBoxes();
		Set<BoundingBox> parkingLotBoxes = seoulPlaceSearchService.getParkingLotBoxes();


		log.info("cafeBoxes {}", cafeBoxes.size());
		log.info("restaurantBoxes {}", restaurantBoxes.size());
		log.info("parkingLotBoxes {}", parkingLotBoxes.size());

		return new ResponseEntity<>(cafeBoxes, HttpStatus.OK);

	}

}
