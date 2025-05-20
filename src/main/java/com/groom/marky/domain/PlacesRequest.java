package com.groom.marky.domain;

import com.groom.marky.domain.google.request.LocationRestriction;

import lombok.Builder;

@Builder
public class PlacesRequest {
	private String textQuery;
	private String languageCode;
	private String regionCode;
	private LocationRestriction locationRestriction;

	@Builder
	public PlacesRequest(String textQuery, String languageCode, String regionCode,
		LocationRestriction locationRestriction) {
		this.textQuery = textQuery;
		this.languageCode = languageCode;
		this.regionCode = regionCode;
		this.locationRestriction = locationRestriction;
	}
}


