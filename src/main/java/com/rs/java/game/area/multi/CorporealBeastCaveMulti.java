package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class CorporealBeastCaveMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Corporeal Beast Cave Multi";
	}

	public CorporealBeastCaveMulti() {
		super(11844, 11588);
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
