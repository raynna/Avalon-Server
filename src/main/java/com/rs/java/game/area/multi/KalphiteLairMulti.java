package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class KalphiteLairMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Kalphite Lair Multi";
	}

	public KalphiteLairMulti() {
		super(13972);
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
