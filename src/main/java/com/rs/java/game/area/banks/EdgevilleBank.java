package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class EdgevilleBank extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Edgeville Bank";
	}

	public EdgevilleBank() {
		super(
				new Polygon(new WorldTile[] { new WorldTile(3098, 3499, 0), new WorldTile(3098, 3488, 0),
				new WorldTile(3091, 3488, 0), new WorldTile(3091, 3499, 0) }) );
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
