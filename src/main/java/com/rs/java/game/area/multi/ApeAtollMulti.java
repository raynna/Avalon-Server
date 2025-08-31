package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class ApeAtollMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "White Wolf Mountain Multi";
	}

	public ApeAtollMulti() {
		super(10794, 10795, 11051, 11050);
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
