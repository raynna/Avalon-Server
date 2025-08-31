package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class CanifisBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Canifis Bank";
	}

	@Override
	public Shape[] shapes() {
		return new Shape[] { new Polygon(new WorldTile[] {
				new WorldTile(3509, 3474, 0),
				new WorldTile(3509, 3478, 0),
				new WorldTile(3508, 3479, 0),
				new WorldTile(3508, 3482, 0),
				new WorldTile(3509, 3483, 0),
				new WorldTile(3516, 3483, 0),
				new WorldTile(3516, 3478, 0),
				new WorldTile(3512, 3474, 0),
				new WorldTile(3509, 3474, 0),
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
