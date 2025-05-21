package com.groom.marky.domain.response;

import java.util.List;

public record GooglePlacesResponse(
	List<Place> places,
	String nextPageToken
) {
	public record Place(
		String id,
		List<String> types,
		String formattedAddress,
		Location location,
		double rating,
		int userRatingCount,
		DisplayName displayName,
		List<Review> reviews
	) {
		public record Location(
			double latitude,
			double longitude
		) {}

		public record DisplayName(
			String text,
			String languageCode
		) {}

		public record Review(
			ReviewText text
		) {
			public record ReviewText(
				String text,
				String languageCode
			) {}
		}
	}
}
