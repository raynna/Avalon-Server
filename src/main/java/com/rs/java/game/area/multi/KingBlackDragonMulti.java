package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class KingBlackDragonMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "King Black Dragon Multi";
	}

	public KingBlackDragonMulti() {
		super(9033);
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
