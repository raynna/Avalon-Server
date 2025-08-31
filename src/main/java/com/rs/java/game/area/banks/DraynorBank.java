package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class DraynorBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Draynor Bank";
	}

	@Override
	public Shape[] shapes() {
		return new Shape[] { new Polygon(new WorldTile[] {
				new WorldTile(3088, 3240, 0),
				new WorldTile(3088, 3246, 0),
				new WorldTile(3097, 3246, 0),
				new WorldTile(3097, 3240, 0),
				new WorldTile(3088, 3240, 0),
		}) };
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
