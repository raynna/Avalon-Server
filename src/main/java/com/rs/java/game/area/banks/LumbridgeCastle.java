package com.rs.java.game.area.banks;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.Shape;
import com.rs.java.game.area.shapes.Polygon;

public final class LumbridgeCastle extends Area {

	@Override
	public Area update() {
		return this;
	}

	@Override
	public String name() {
		return "Lumbridge Castle & Yard";
	}

	public LumbridgeCastle() {
		super(
				new Polygon(new WorldTile[] {
				new WorldTile(3229, 3217, 0),//done
				new WorldTile(3230, 3216, 0),//done
				new WorldTile(3230, 3212, 0),//done
				new WorldTile(3226, 3212, 0),
				new WorldTile(3226, 3208, 0),
				new WorldTile(3221, 3203, 0),
				new WorldTile(3214, 3203, 0),
				new WorldTile(3212, 3201, 0),
				new WorldTile(3204, 3201, 0),
				new WorldTile(3201, 3204, 0),
				new WorldTile(3201, 3216, 0),
				new WorldTile(3197, 3216, 0),
				new WorldTile(3197, 3221, 0),
				new WorldTile(3201, 3221, 0),
				new WorldTile(3201, 3233, 0),
				new WorldTile(3204, 3236, 0),
				new WorldTile(3212, 3236, 0),
				new WorldTile(3214, 3234, 0),
				new WorldTile(3220, 3234, 0),
				new WorldTile(3226, 3228, 0),
				new WorldTile(3226, 3225, 0),
				new WorldTile(3230, 3225, 0),
				new WorldTile(3230, 3221, 0),
				new WorldTile(3229, 3220, 0),
				new WorldTile(3229, 3217, 0),
		}) );
	}

	@Override
	public boolean member() {
		return false;
	}

	@Override
	public Environment environment() {
		return Environment.SAFEZONE;
	}

}
