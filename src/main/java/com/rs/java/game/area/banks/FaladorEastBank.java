package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class FaladorEastBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Falador East Bank";
	}

	public FaladorEastBank() {
		super(
				new Polygon(new WorldTile[] {
				new WorldTile(3009, 3353, 0),
				new WorldTile(3009, 3358, 0),
				new WorldTile(3018, 3358, 0),
				new WorldTile(3018, 3356, 0),
				new WorldTile(3021, 3356, 0),
				new WorldTile(3021, 3353, 0),
				new WorldTile(3009, 3353, 0),
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
