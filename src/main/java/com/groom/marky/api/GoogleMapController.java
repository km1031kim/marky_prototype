package com.groom.marky.api;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.groom.marky.common.BoundingBox;
import com.groom.marky.common.BoundingCircle;
import com.groom.marky.domain.GooglePlacesResponse;
import com.groom.marky.service.impl.GooglePlaceSearchServiceImpl;

@Controller
@RequestMapping("/api")
public class GoogleMapController {

	private final GooglePlaceSearchServiceImpl googlePlaceSearchService;

	@Autowired
	public GoogleMapController(GooglePlaceSearchServiceImpl googlePlaceSearchService) {
		this.googlePlaceSearchService = googlePlaceSearchService;
	}


	@GetMapping("/google")
	public ResponseEntity<?> callKeywordSearchApi() {

		// 126.91844127777779,37.550798433333334,126.92141475000001,37.552475316666666, total : 42

		BoundingBox box = new BoundingBox(
			126.91844127777779,
			37.550798433333334,
			126.92141475000001,
			37.552475316666666);

		// 5건 나오네..


		GooglePlacesResponse response = googlePlaceSearchService.searchNearby("식당", box);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
