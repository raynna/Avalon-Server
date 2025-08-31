package com.rs.java.game.area.multi;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Rectangle;

public class CastleWarsMulti extends Area {

	public CastleWarsMulti() {
		super(9520);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Castle Wars Multi";
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