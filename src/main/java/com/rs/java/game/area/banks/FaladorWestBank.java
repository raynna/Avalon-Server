package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class FaladorWestBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Falador West Bank";
	}

	public FaladorWestBank() {
		super(
				new Polygon(new WorldTile[] {
				new WorldTile(2943, 3368, 0),
				new WorldTile(2943, 3373, 0),
				new WorldTile(2947, 3373, 0),
				new WorldTile(2947, 3369, 0),
				new WorldTile(2949, 3369, 0),
				new WorldTile(2949, 3368, 0),
				new WorldTile(2943, 3368, 0),
		}) );
	}

	@Override
	public boolean member() {
		return false;
	}

	@Override
	public Environment environment() {
		return Environment.SAFEZONE;
	}

}
