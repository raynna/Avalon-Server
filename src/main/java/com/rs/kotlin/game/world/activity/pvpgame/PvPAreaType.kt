package com.rs.kotlin.game.world.activity.pvpgame

import com.rs.java.game.WorldTile

enum class PvPAreaType(
    val southWestTile: WorldTile,
    val northEastTile: WorldTile,
    val firstSpawn: WorldTile? = null,
    val secondSpawn: WorldTile? = null,
    val lobby1Tile: WorldTile? = null,
    val lobby2Tile: WorldTile? = null,
) {
    CLASSIC(
        WorldTile(2752, 5888, 0), WorldTile(2815, 6015, 0),
        firstSpawn = WorldTile(2891, 5515, 0),
        secondSpawn = WorldTile(2933, 5558, 0),
        lobby1Tile = WorldTile(2908, 5536, 0), // exact coords inside original map
        lobby2Tile = WorldTile(2915, 5537, 0)
    ),
    PLATEAU(
        WorldTile(2831, 5888, 0), WorldTile(2928, 5951, 0),
        firstSpawn = WorldTile(2891, 5515, 0),
        secondSpawn = WorldTile(2933, 5558, 0),
        lobby1Tile = WorldTile(2908, 5536, 0), // exact coords inside original map
        lobby2Tile = WorldTile(2915, 5537, 0)
    ),
    FORSAKEN_QUARRY(
        WorldTile(2880, 5504, 0), WorldTile(2943, 5567, 0),
        firstSpawn = WorldTile(2891, 5515, 0),
        secondSpawn = WorldTile(2933, 5558, 0),
        lobby1Tile = WorldTile(2908, 5536, 0), // exact coords inside original map
        lobby2Tile = WorldTile(2915, 5537, 0)
    ),
    BLASTED_FOREST(
        WorldTile(2880, 5632, 0), WorldTile(2942, 5695, 0),
        firstSpawn = WorldTile(2891, 5515, 0),
        secondSpawn = WorldTile(2933, 5558, 0),
        lobby1Tile = WorldTile(2908, 5536, 0), // exact coords inside original map
        lobby2Tile = WorldTile(2915, 5537, 0)
    ),
    TURRETS(
        WorldTile(2689, 5505, 0), WorldTile(2750, 5630, 0),
        firstSpawn = WorldTile(2891, 5515, 0),
        secondSpawn = WorldTile(2933, 5558, 0),
        lobby1Tile = WorldTile(2908, 5536, 0), // exact coords inside original map
        lobby2Tile = WorldTile(2915, 5537, 0)
    );
}
