package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class YanilleBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Yanille Bank";
	}

	@Override
	public Shape[] shapes() {
		return new Shape[] { new Polygon(new WorldTile[] {
				new WorldTile(2609, 3088, 0),
				new WorldTile(2609, 3097, 0),
				new WorldTile(2616, 3097, 0),
				new WorldTile(2616, 3088, 0),
				new WorldTile(2609, 3088, 0),
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
