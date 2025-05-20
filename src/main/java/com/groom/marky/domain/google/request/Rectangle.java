package com.groom.marky.domain.google.request;

import lombok.Builder;

@Builder
public class Rectangle implements LocationRestriction {

	private Coordinate low;
	private Coordinate high;

	public Rectangle(Coordinate low, Coordinate high) {
		this.low = low;
		this.high = high;
	}
}
