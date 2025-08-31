package com.rs.java.game.area.multi;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Rectangle;

public class BarbarianVillageMulti extends Area {

	public BarbarianVillageMulti() {
		super(new Integer[]{12341},
				// North of falador party room
				new Rectangle(new WorldTile(3048, 3392, 0), new WorldTile(3055, 3407, 0)),
				//west of barbarian village
				new Rectangle(new WorldTile(3056, 3408, 0), new WorldTile(3071, 3447, 0))
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Barbarian Village Multi";
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