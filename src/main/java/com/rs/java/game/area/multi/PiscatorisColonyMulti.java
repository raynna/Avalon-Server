package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class PiscatorisColonyMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Piscatoris Colony Multi";
	}

	public PiscatorisColonyMulti() {
		super(9273);
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
