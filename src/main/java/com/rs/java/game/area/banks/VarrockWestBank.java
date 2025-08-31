package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class VarrockWestBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Varrock West Bank";
	}

	public VarrockWestBank() {
		super(
				new Polygon(new WorldTile[] {
				new WorldTile(3179, 3432, 0), new WorldTile(3179, 3446, 0),
				new WorldTile(3194, 3446, 0), new WorldTile(3194, 3432, 0) }) );
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
