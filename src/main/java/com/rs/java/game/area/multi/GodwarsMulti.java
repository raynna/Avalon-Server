package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class GodwarsMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Godwars Multi";
	}

	public GodwarsMulti() {
		super(11345, 11346, 11347, 11601, 11602, 11603);
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
