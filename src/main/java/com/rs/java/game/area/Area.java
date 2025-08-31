package com.rs.java.game.area;

import java.util.*;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;

public abstract class Area {

	private final List<Shape> shapes;
	private final List<Integer> regionIds;
	private transient List<Player> players = new LinkedList<>();

	/** Construct an area with shapes only */
	protected Area(Shape... shapes) {
		this.shapes = Arrays.asList(shapes);
		this.regionIds = new ArrayList<>();
	}

	/** Construct an area with regions only */
	protected Area(Integer... regionIds) {
		this.shapes = new ArrayList<>();
		this.regionIds = Arrays.asList(regionIds);
	}

	/** Construct an area with both regions and shapes */
	protected Area(Integer[] regionIds, Shape... shapes) {
		this.shapes = Arrays.asList(shapes);
		this.regionIds = Arrays.asList(regionIds);
	}

	public abstract Area update();
	public abstract String name();
	public abstract boolean member();
	public abstract Environment environment();

	public List<Shape> shapes() {
		return shapes;
	}

	public List<Integer> regions() {
		return regionIds;
	}

	/** Supports shapes AND regions */
	public boolean contains(WorldTile tile) {
		// Region check first (fast O(1))
		if (!regionIds.isEmpty() && regionIds.contains(tile.getRegionId())) {
			return true;
		}
		// Shape check
		for (Shape shape : shapes) {
			if (shape.inside(tile)) {
				return true;
			}
		}
		return false;
	}

	public List<Player> players() {
		return players;
	}

	public enum Environment {
		NORMAL, DESERT, SAFEZONE, MULTI, WILDERNESS, WILDERNESS_SAFE;
	}
}
