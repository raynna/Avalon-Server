package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class PestControlMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Pest Control Multi";
	}

	public PestControlMulti() {
		super(10536);
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
