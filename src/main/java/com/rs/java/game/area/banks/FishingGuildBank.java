package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class FishingGuildBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Fishing Guild Bank";
	}

	public FishingGuildBank() {
		super(
				new Polygon(new WorldTile[] {
				new WorldTile(2583, 3420, 0),
				new WorldTile(2583, 3424, 0),
				new WorldTile(2587, 3424, 0),
				new WorldTile(2587, 3420, 0),
				new WorldTile(2583, 3420, 0),
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
