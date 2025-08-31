package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class CatherbyBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Catherby Bank";
	}

	public CatherbyBank() {
		super(
				new Polygon(new WorldTile[] {
						new WorldTile(2806, 3438, 0),
						new WorldTile(2806, 3441, 0),
						new WorldTile(2812, 3441, 0),
						new WorldTile(2812, 3438, 0),
						new WorldTile(2806, 3438, 0) // closing point
				})
		);
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
