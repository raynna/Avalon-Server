package com.rs.java.game.area.multi;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.shapes.Rectangle;

public class WildernessMulti extends Area {

	public WildernessMulti() {
		super(new Integer[]{12599, 12855, 13111, 12600, 12856, 13112, 12857, 13113, 12858, 13114, 12859, 13115, 12860, 13116, 13372, 12861, 13117, 13373, 12088, 12089},
				// North of moss giants
				new Rectangle(new WorldTile(3136, 3840, 0), new WorldTile(3199, 3903, 0)),
				// Small bit of north-east lava maze
				new Rectangle(new WorldTile(3112, 3873, 0), new WorldTile(3135, 3903, 0)),
				// North of lava maze
				new Rectangle(new WorldTile(3072, 3879, 0), new WorldTile(3135, 3903, 0)),
				// KBD lair
				new Rectangle(new WorldTile(3008, 3856, 0), new WorldTile(3048, 3903, 0)),
				// Wilderness agility
				new Rectangle(new WorldTile(2984, 3912, 0), new WorldTile(3007, 3928, 0), "Wilderness Agility"),
				// Chaos altar
				new Rectangle(new WorldTile(2946, 3816, 0), new WorldTile(2959, 3830, 0))
		);
	}

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Wilderness Multi Areas";
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