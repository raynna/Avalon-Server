package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class ArdougneNorthBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Ardougne North Bank";
	}

	@Override
	public Shape[] shapes() {
		return new Shape[] { new Polygon(new WorldTile[] {
				new WorldTile(2612, 3330, 0),
				new WorldTile(2612, 3335, 0),
				new WorldTile(2621, 3335, 0),
				new WorldTile(2621, 3330, 0),
				new WorldTile(2612, 3330, 0),
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
