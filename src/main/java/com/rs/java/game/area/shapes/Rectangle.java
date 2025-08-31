package com.rs.java.game.area.shapes;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Shape;

public class Rectangle extends Shape {

	private final WorldTile sw;
	private final WorldTile ne;
	private final String debugLabel;

	public Rectangle(WorldTile southWest, WorldTile northEast) {
		this(southWest, northEast, null);
	}

	public Rectangle(WorldTile southWest, WorldTile northEast, String debugLabel) {
		this.sw = southWest;
		this.ne = northEast;
		this.debugLabel = debugLabel;

		areas(new WorldTile[]{ ne, sw }).type(ShapeType.RECTANGLE);

	}

	@Override
	public boolean inside(WorldTile location) {
		boolean inPlane = location.getPlane() == sw.getPlane() && location.getPlane() == ne.getPlane();
		boolean inX = location.getX() >= sw.getX() && location.getX() <= ne.getX();
		boolean inY = location.getY() >= sw.getY() && location.getY() <= ne.getY();

        return inPlane && inX && inY;
	}


	public WorldTile getSouthWest() { return sw; }
	public WorldTile getNorthEast() { return ne; }
}
