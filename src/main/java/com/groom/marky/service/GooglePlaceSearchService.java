package com.groom.marky.service;

import java.util.List;

import com.groom.marky.domain.request.Rectangle;
import com.groom.marky.domain.response.GooglePlacesApiResponse;

public interface GooglePlaceSearchService {

	List<String> search(String text, Rectangle box);

	GooglePlacesApiResponse searchNearby(List<String> types, Rectangle box);

}
