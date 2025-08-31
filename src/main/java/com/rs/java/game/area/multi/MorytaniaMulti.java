package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class MorytaniaMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Morytania Multi";
	}

	public MorytaniaMulti() {
		super(13876);
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
