package com.rs.java.game.area.multi;

import com.rs.java.game.area.Area;

public final class WaterbirthDungeonMulti extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Waterbirth Dungeon Multi";
	}

	public WaterbirthDungeonMulti() {
		super(9886, 10142, 7236, 7492, 7748, 11589);
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
