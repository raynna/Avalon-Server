package com.rs.java.game.area.shapes;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Shape;

public class Polygon extends Shape {

	private int sides;
	private int[][] points;

	public Polygon(WorldTile[] points) {
		sides(points.length).areas(points).type(ShapeType.POLYGON);

		points(new int[sides][2]);

		for (int i = 0; i < sides; i++) {
			points()[i][0] = points[i].getX();
			points()[i][1] = points[i].getY();
		}

	}

	@Override
	public boolean inside(WorldTile location) {
		int x = location.getX();
		int y = location.getY();

		for (int i = 0, j = sides - 1; i < sides; j = i++) {
			int xi = points[i][0], yi = points[i][1];
			int xj = points[j][0], yj = points[j][1];

			if (y >= Math.min(yi, yj) && y <= Math.max(yi, yj)) {
				if (yj != yi) {
					double xOnEdge = xi + (double)(xj - xi) * (y - yi) / (double)(yj - yi);
					if (Math.abs(x - xOnEdge) < 1e-9) {
						return true;
					}
				}
			}
			if (x >= Math.min(xi, xj) && x <= Math.max(xi, xj)) {
				if (xj != xi) {
					double yOnEdge = yi + (double)(yj - yi) * (x - xi) / (double)(xj - xi);
					if (Math.abs(y - yOnEdge) < 1e-9) {
						return true;
					}
				}
			}
		}

		boolean inside = false;
		for (int i = 0, j = sides - 1; i < sides; j = i++) {
			int xi = points[i][0], yi = points[i][1];
			int xj = points[j][0], yj = points[j][1];

			boolean intersect = ((yi > y) != (yj > y)) &&
					(x <= (double)(xj - xi) * (y - yi) / (double)(yj - yi) + xi);
			if (intersect)
				inside = !inside;
		}
		return inside;
	}

	public int sides() {
		return sides;
	}

	public Shape sides(int sides) {
		this.sides = sides;
		return this;
	}

	public int[][] points() {
		return points;
	}

	public Shape points(int[][] points) {
		this.points = points;
		return this;
	}
}