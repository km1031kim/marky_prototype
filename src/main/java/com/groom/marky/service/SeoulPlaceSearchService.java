package com.groom.marky.service;

import static com.groom.marky.domain.KakaoMapCategoryGroupCode.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.groom.marky.common.BoundingBox;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SeoulPlaceSearchService {

	private final KakaoPlaceSearchService kakaoPlaceSearchService;
	private static final BoundingBox seoulBox = BoundingBox.boxOfSeoul();
	private static final List<BoundingBox> seoulBoxes = seoulBox.generateGrid(10, 10);

	@Autowired
	public SeoulPlaceSearchService(KakaoPlaceSearchService kakaoPlaceSearchService) {
		this.kakaoPlaceSearchService = kakaoPlaceSearchService;
	}

	public Map<String, String> collectParkingLot() {
		return kakaoPlaceSearchService.searchAll(seoulBoxes, PK6);
	}

	public Map<String, String> collectCafe() {
		return kakaoPlaceSearchService.searchAll(seoulBoxes, CE7);
	}

	public Map<String, String> collectRestaurant() {
		return kakaoPlaceSearchService.searchAll(seoulBoxes, FD6);
	}

	public Set<BoundingBox> getCafeBoxes() {
		return kakaoPlaceSearchService.getBoxes(seoulBoxes, CE7);
	}

	public Set<BoundingBox> getRestaurantBoxes() {
		return kakaoPlaceSearchService.getBoxes(seoulBoxes, FD6);
	}

	public Set<BoundingBox> getParkingLotBoxes() {
		return kakaoPlaceSearchService.getBoxes(seoulBoxes, PK6);
	}

}

