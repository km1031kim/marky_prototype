package com.groom.marky.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.groom.marky.domain.google.request.Coordinate;
import com.groom.marky.domain.google.request.LocationRestriction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rectangle implements LocationRestriction {

	private Coordinate low;
	private Coordinate high;

/*
	private final double west;   // 경도 최소 (xmin)
	private final double south;  // 위도 최소 (ymin)
	private final double east;   // 경도 최대 (xmax)
	private final double north;  // 위도 최대 (ymax)
*/

	public Rectangle(double west, double south, double east, double north) {
		low = new Coordinate(south, west);
		high = new Coordinate(north, east);
	}

	public static Rectangle rectOfSeoul() {
		return new Rectangle(
			SeoulBounds.MIN_LNG, SeoulBounds.MIN_LAT, SeoulBounds.MAX_LNG, SeoulBounds.MAX_LAT);
	}

	/**
	 * @param rows 세로 분할 개수 (위도 방향 칸 수)
	 * @param cols 가로 분할 개수 (경도 방향 칸 수)
	 * @return 서브 BoundingBox 리스트 (크기 = rows*cols)
	 */
	public List<Rectangle> generateGrid(int rows, int cols) {
		if (rows <= 0 || cols <= 0) {
			throw new IllegalArgumentException("rows, cols must be > 0");
		}

		List<Rectangle> grid = new ArrayList<>(rows * cols);
		double latStep = (north - south) / rows;
		double lngStep = (east - west) / cols;

		for (int i = 0; i < rows; i++) {

			// south 경계
			double cellSouth = south + i * latStep;

			// 마지막 행이면 north 그대로, 아니면 step 만큼 올린 값
			double cellNorth = (i == rows - 1 ? north : cellSouth + latStep);

			for (int j = 0; j < cols; j++) {

				// west 경계
				double cellWest = west + j * lngStep;
				// 마지막 열이면 east 그대로, 아니면 step 만큼 더한 값
				double cellEast = (j == cols - 1 ? east : cellWest + lngStep);
				grid.add(new Rectangle(cellWest, cellSouth, cellEast, cellNorth));
			}
		}
		return grid;
	}

	/**
	 * 하나의 그리드를 4개의 그리드로 분할.
	 */
	public List<Rectangle> splitGrid() {
		double midLat = (south + north) / 2;
		double midLng = (west + east) / 2;

		ArrayList<Rectangle> splitGrids = new ArrayList<>();

		// 남서
		splitGrids.add(new Rectangle(west, south, midLng, midLat));

		// 남동
		splitGrids.add(new Rectangle(midLng, south, east, midLat));

		// 북서
		splitGrids.add(new Rectangle(west, midLat, midLng, north));

		// 북동
		splitGrids.add(new Rectangle(midLng, midLat, east, north));

		return splitGrids;
	}

	@Override
	public String toString() {
		return west + "," + south + "," + east + "," + north;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass())
			return false;
		Rectangle that = (Rectangle)object;
		return Double.compare(west, that.west) == 0 && Double.compare(south, that.south) == 0
			&& Double.compare(east, that.east) == 0 && Double.compare(north, that.north) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(west, south, east, north);
	}
}
