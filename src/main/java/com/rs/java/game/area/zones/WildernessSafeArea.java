package com.rs.java.game.area.zones;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Rectangle;

public class WildernessSafeArea extends Area {

	public WildernessSafeArea() {
		super(new Rectangle(
				new WorldTile(2944, 3520, 0),
				new WorldTile(3327, 3524, 0)
			)
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Wilderness Safe Areas";
	}

	@Override
	public boolean member() {
		return false;
	}

	@Override
	public Environment environment() {
		return Environment.WILDERNESS_SAFE;
	}
}