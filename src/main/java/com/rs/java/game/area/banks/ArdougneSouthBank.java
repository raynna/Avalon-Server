package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Polygon;

public final class ArdougneSouthBank extends Area {

	public ArdougneSouthBank() {
		super(
				new Polygon(new WorldTile[] {
						new WorldTile(2649, 3280, 0),
						new WorldTile(2649, 3287, 0),
						new WorldTile(2658, 3287, 0),
						new WorldTile(2658, 3280, 0),
						new WorldTile(2649, 3280, 0) // closed polygon
				})
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Ardougne South Bank";
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
