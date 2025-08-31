package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Polygon;

public final class CastleWarsBank extends Area {

	public CastleWarsBank() {
		super(
				new Polygon(new WorldTile[] {
						new WorldTile(2440, 3082, 0),
						new WorldTile(2438, 3084, 0),
						new WorldTile(2439, 3085, 0),
						new WorldTile(2439, 3087, 0),
						new WorldTile(2438, 3088, 0),
						new WorldTile(2438, 3091, 0),
						new WorldTile(2439, 3092, 0),
						new WorldTile(2439, 3094, 0),
						new WorldTile(2438, 3095, 0),
						new WorldTile(2440, 3097, 0),
						new WorldTile(2444, 3097, 0),
						new WorldTile(2445, 3098, 0),
						new WorldTile(2447, 3098, 0),
						new WorldTile(2447, 3095, 0),
						new WorldTile(2445, 3095, 0),
						new WorldTile(2445, 3092, 0),
						new WorldTile(2446, 3091, 0),
						new WorldTile(2446, 3088, 0),
						new WorldTile(2445, 3087, 0),
						new WorldTile(2445, 3082, 0),
						new WorldTile(2440, 3082, 0)
				})
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Castle Wars Bank";
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
