package raynna.game.player.content;

import raynna.core.thread.CoresManager;
import raynna.game.WorldTile;
import raynna.game.map.MapBuilder;

public class EdgevillePvPInstance {

	private static int[] boundChuncks;

	public static void buildMap() {
		int[] map = new int[] { 384, 435 };
		MapBuilder.copyAllPlanesMap(map[0], map[1], boundChuncks[0], boundChuncks[1], 8);
	}

	public static WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(boundChuncks[0] * 8 + mapX, boundChuncks[1] * 8 + mapY, 0);
	}

	public static void buildInstance() {
		Runnable event = new Runnable() {
			@Override
			public void run() {
				CoresManager.getSlowExecutor().execute(() -> {
                    if (boundChuncks == null) {
                        boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
                        buildMap();
                    } else {
                        buildMap();
                    }
                });

			}
		};
		event.run();

	}

}
