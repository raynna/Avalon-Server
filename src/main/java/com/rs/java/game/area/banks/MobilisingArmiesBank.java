package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class MobilisingArmiesBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Mobilising Armies Bank";
	}

	@Override
	public Shape[] shapes() {
		return new Shape[] { new Polygon(new WorldTile[] {
				new WorldTile(2440, 2840, 0),
				new WorldTile(2440, 2843, 0),
				new WorldTile(2405, 2843, 0),
				new WorldTile(2405, 2840, 0),
				new WorldTile(2440, 2840, 0),
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
