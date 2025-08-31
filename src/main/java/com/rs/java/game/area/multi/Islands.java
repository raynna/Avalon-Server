package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class Islands extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Isles Multi";
	}

	public Islands() {
		super(9276, 9532, 8763, 8253, 8509, 8252, 8508);
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
