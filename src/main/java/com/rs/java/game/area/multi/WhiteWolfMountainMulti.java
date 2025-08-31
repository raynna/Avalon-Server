package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class WhiteWolfMountainMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "White Wolf Mountain Multi";
	}

	public WhiteWolfMountainMulti() {
		super(11318);
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
