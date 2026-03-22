package raynna.game.player;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.item.FloorItem;
import raynna.game.npc.NPC;
import raynna.game.route.RouteFinder;
import raynna.game.route.RouteStrategy;
import raynna.game.route.strategy.EntityStrategy;
import raynna.game.route.strategy.FixedTileStrategy;
import raynna.game.route.strategy.FloorItemStrategy;
import raynna.game.route.strategy.ObjectStrategy;
import raynna.util.Utils;

public class RouteEvent {

	/**
	 * Object to which we are finding the route.
	 */
	private Object object;
	/**
	 * The event instance.
	 */
	private Runnable event;
	/**
	 * Whether we also run on alternative.
	 */
	private boolean alternative;
	/**
	 * Contains last route strategies.
	 */
	private RouteStrategy[] last;

	public RouteEvent(Object object, Runnable event) {
		this(object, event, false);
	}

	public RouteEvent(Object object, Runnable event, boolean alternative) {
		this.object = object;
		this.event = event;
		this.alternative = alternative;
	}

	public boolean processEvent(final NPC npc) {
		if (!simpleCheck(npc)) {
			return true;
		}

		if (npc.isFrozen())
			return true;
		RouteStrategy[] strategies = generateStrategies();
		if (strategies == null)
			return false;
		else if (last != null && match(strategies, last) && npc.hasWalkSteps())
			return false;
		else if (last != null && match(strategies, last) && !npc.hasWalkSteps()) {
			for (int i = 0; i < strategies.length; i++) {
				RouteStrategy strategy = strategies[i];
				int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, npc.getX(), npc.getY(), npc.getPlane(),
						npc.getSize(), strategy, i == (strategies.length - 1));
				if (steps == -1)
					continue;
				if ((!RouteFinder.lastIsAlternative() && steps <= 0) || alternative) {
					event.run();
					return true;
				}
			}
			return true;
		} else {
			last = strategies;

			for (int i = 0; i < strategies.length; i++) {
				RouteStrategy strategy = strategies[i];
				int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, npc.getX(), npc.getY(), npc.getPlane(),
						npc.getSize(), strategy, i == (strategies.length - 1));
				if (steps == -1)
					continue;
				if ((!RouteFinder.lastIsAlternative() && steps <= 0)) {
					event.run();
					return true;
				}
				int[] bufferX = RouteFinder.getLastPathBufferX();
				int[] bufferY = RouteFinder.getLastPathBufferY();
				npc.resetWalkSteps();
				if (npc.isFrozen())
					return false;
				for (int step = steps - 1; step >= 0; step--) {
					if (!npc.addWalkSteps(bufferX[step], bufferY[step], 25, true))
						break;
				}

				return false;
			}
			return true;
		}
	}


	public boolean checkInteraction(Player player) {

		if (player.hasWalkSteps())
			return false;

		RouteStrategy[] strategies = generateStrategies();
		if (last == null || !match(strategies, last))
			return false;

		for (int i = 0; i < strategies.length; i++) {

			RouteStrategy strategy = strategies[i];

			int steps = RouteFinder.findRoute(
					RouteFinder.WALK_ROUTEFINDER,
					player.getX(),
					player.getY(),
					player.getPlane(),
					player.getSize(),
					strategy,
					i == (strategies.length - 1)
			);

			if (steps == -1)
				continue;

			if ((!RouteFinder.lastIsAlternative() && steps <= 0) || alternative) {

				if (alternative)
					player.getPackets().sendResetMinimapFlag();

				if (player.getNextFaceEntity() != -1)
					player.setNextFaceEntity(null);

				event.run();
				return true;
			}
		}

		return false;
	}

	public boolean processEvent(Player player) {
		if (!simpleCheck(player)) {
			return true;
		}
		RouteStrategy[] strategies = generateStrategies();
		if (strategies == null)
			return false;

		if (last != null && match(strategies, last) && player.hasWalkSteps())
			return false;

		last = strategies;
		for (RouteStrategy strategy : strategies) {

			int steps = RouteFinder.findRoute(
					RouteFinder.WALK_ROUTEFINDER,
					player.getX(), player.getY(),
					player.getPlane(),
					player.getSize(),
					strategy,
					true
			);

			if (steps == -1)
				continue;

			// movement setup
			int[] bufferX = RouteFinder.getLastPathBufferX();
			int[] bufferY = RouteFinder.getLastPathBufferY();

			player.resetWalkSteps();

			for (int step = steps - 1; step >= 0; step--) {
				if (!player.addWalkSteps(bufferX[step], bufferY[step], 25, true))
					break;
			}

			return false;
		}

		return true;
	}

	private boolean simpleCheck(Player player) {
		if (object == null)
			return false;
		if (object instanceof Entity) {
			return player.getPlane() == ((Entity) object).getPlane();
		} else if (object instanceof WorldObject) {
			return player.getPlane() == ((WorldObject) object).getPlane();
		} else if (object instanceof FloorItem) {
			return player.getPlane() == ((FloorItem) object).getTile().getPlane();
		} else if (object instanceof WorldTile) {
			return player.getPlane() == ((WorldTile) object).getTile().getPlane();

		} else {
			throw new RuntimeException(object + " is not instanceof any reachable entity.");
		}
	}

	private boolean simpleCheck(NPC npc) {
		if (object == null)
			return false;
		if (object instanceof Entity) {
			return npc.getPlane() == ((Entity) object).getPlane();
		} else if (object instanceof WorldObject) {
			return npc.getPlane() == ((WorldObject) object).getPlane();
		} else if (object instanceof FloorItem) {
			return npc.getPlane() == ((FloorItem) object).getTile().getPlane();
		} else if (object instanceof WorldTile) {
			return npc.getPlane() == ((WorldTile) object).getTile().getPlane();
		} else {
			throw new RuntimeException(object + " is not instanceof any reachable entity.");
		}
	}

	private RouteStrategy[] generateStrategies() {
		if (object == null)
			return last;
		if (object instanceof Entity) {
			return new RouteStrategy[] { new EntityStrategy((Entity) object) };
		} else if (object instanceof WorldObject) {
			return new RouteStrategy[] { new ObjectStrategy((WorldObject) object) };
		} else if (object instanceof FloorItem item) {
            return new RouteStrategy[] { new FixedTileStrategy(item.getTile().getX(), item.getTile().getY()),
					new FloorItemStrategy(item) };
		} else if (object instanceof WorldTile tile) {
            return new RouteStrategy[] {
					new FixedTileStrategy(tile.getX(), tile.getY())
			};
		} else {
			throw new RuntimeException(object + " is not instanceof any reachable entity.");
		}
	}

	private boolean match(RouteStrategy[] a1, RouteStrategy[] a2) {
		if (a1.length != a2.length)
			return false;
		for (int i = 0; i < a1.length; i++)
			if (!a1[i].equals(a2[i]))
				return false;
		return true;
	}

	private static boolean canInteract(Player player, int targetX, int targetY, int plane, int npcSize,
									   boolean allowDiagonal, int reach) {
		if (player.getPlane() != plane)
			return false;

		int dx = Math.abs(player.getX() - targetX);
		int dy = Math.abs(player.getY() - targetY);

		// Too far
		if (dx > reach || dy > reach)
			return false;

		// For 1x1, diagonal adjacency is the main offender (dx==1 && dy==1)
		if (!allowDiagonal && npcSize == 1 && dx == 1 && dy == 1)
			return false;

		// Otherwise: withinDistance-style (square range)
		return true;
	}

	private static WorldTile getNpcNextTile(NPC npc) {
		if (!npc.hasWalkSteps())
			return new WorldTile(npc.getX(), npc.getY(), npc.getPlane());

		int dir = npc.getNextWalkDirection(); // <-- if your base uses a different name, change this
		if (dir < 0)
			return new WorldTile(npc.getX(), npc.getY(), npc.getPlane());

		int nextX = npc.getX() + Utils.DIRECTION_DELTA_X[dir];
		int nextY = npc.getY() + Utils.DIRECTION_DELTA_Y[dir];
		return new WorldTile(nextX, nextY, npc.getPlane());
	}

	public boolean isEntityTarget() {
		return object instanceof Entity;
	}

	public boolean isTileTarget() {
		return object instanceof WorldObject
				|| object instanceof FloorItem
				|| object instanceof WorldTile;
	}

}
