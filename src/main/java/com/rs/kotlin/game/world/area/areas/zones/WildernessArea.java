package com.rs.kotlin.game.world.area.areas.zones;

import com.rs.kotlin.game.world.area.Area;

public class WildernessArea extends Area {

	public WildernessArea() {
		super(new Integer[]{
				11831, 11832, 11833, 11834, 11835, 11836, 11837,
				12087, 12088, 12089, 12090, 12091, 12092, 12093,
				12343, 12344, 12345, 12346, 12347, 12348, 12349,
				12599, 12600, 12601, 12602, 12603, 12604, 12605,
				12855, 12856, 12857, 12858, 12859, 12860, 12861,
				13111, 13112, 13113, 13114, 13115, 13116, 13117,
				13367, 13368, 13369, 13370, 13371, 13372, 13373
				}
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Wilderness Dangerous Areas";
	}

	@Override
	public boolean member() {
		return false;
	}

	@Override
	public Environment environment() {
		return Environment.WILDERNESS;
	}
}