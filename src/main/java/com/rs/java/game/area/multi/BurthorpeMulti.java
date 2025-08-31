package com.rs.java.game.area.multi;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Polygon;
import com.rs.java.game.area.shapes.Rectangle;

public class BurthorpeMulti extends Area {

	public BurthorpeMulti() {
		super(
				new Rectangle(new WorldTile(2880, 3520, 0), new WorldTile(2903, 3543, 0))

		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Burthorpe Multi";
	}

	@Override
	public boolean member() {
		return false;
	}

	@Override
	public Environment environment() {
		return Environment.MULTI;
	}
}