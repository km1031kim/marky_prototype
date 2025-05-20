package com.groom.marky.service;

import com.groom.marky.common.BoundingBox;
import com.groom.marky.common.BoundingCircle;
import com.groom.marky.domain.GooglePlacesResponse;

public interface GooglePlaceSearchService {

	GooglePlacesResponse search(String text, BoundingBox box);

	GooglePlacesResponse searchNearby(String text, BoundingBox box);

}
