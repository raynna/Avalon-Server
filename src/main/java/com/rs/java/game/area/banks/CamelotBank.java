package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Polygon;

public final class CamelotBank extends Area {

	public CamelotBank() {
		super(
				new Polygon(new WorldTile[] {
						new WorldTile(2724, 3487, 0),
						new WorldTile(2724, 3490, 0),
						new WorldTile(2721, 3490, 0),
						new WorldTile(2721, 3494, 0),
						new WorldTile(2719, 3494, 0),
						new WorldTile(2719, 3496, 0),
						new WorldTile(2721, 3496, 0),
						new WorldTile(2721, 3497, 0),
						new WorldTile(2730, 3497, 0),
						new WorldTile(2730, 3490, 0),
						new WorldTile(2727, 3490, 0),
						new WorldTile(2727, 3487, 0),
						new WorldTile(2724, 3487, 0) // closing point
				})
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Camelot Bank";
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
