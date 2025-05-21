package com.groom.marky.domain.google.request;

import com.groom.marky.common.Rectangle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coordinate {
	private double latitude;
	private double longitude;

	public Coordinate(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/*public static LocationRestriction from(Rectangle box) {
		// 1) BoundingBox → Coordinate 변환
		Coordinate low = new Coordinate(box.getSouth(), box.getWest());
		Coordinate high = new Coordinate(box.getNorth(), box.getEast());

		return new RectangleTobe(low, high);
	}*/

}

