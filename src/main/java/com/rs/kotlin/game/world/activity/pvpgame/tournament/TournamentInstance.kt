package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.World
import com.rs.java.game.WorldObject
import com.rs.java.game.WorldTile
import com.rs.java.game.map.MapBuilder
import com.rs.java.game.player.Player
import com.rs.kotlin.game.world.activity.pvpgame.PvPAreaType
import com.rs.kotlin.game.world.util.Msg

class TournamentInstance {
    private val lobby = TournamentLobby(this)
    private var boundChunks: IntArray? = null

    fun create() {
        val area = PvPAreaType.FORSAKEN_QUARRY
        val width = (area.northEastTile.x - area.southWestTile.x) / 8 + 1
        val height = (area.northEastTile.y - area.southWestTile.y) / 8 + 1
        boundChunks = MapBuilder.findEmptyChunkBound(width, height)
        MapBuilder.copyAllPlanesMap(
            area.southWestTile.chunkX, area.southWestTile.chunkY,
            boundChunks!![0], boundChunks!![1], width, height
        )
    }

    fun addPlayer(player: Player) {
        val tile = getLobby1()
        player.nextWorldTile = tile
        lobby.addPlayer(player)
        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                clearClutter()
                stop()
            }
        }, 1)
    }

    fun end(winner: Player?) {
        if (winner != null) {
            Msg.world(Msg.ORANGE, icon = 22,"${winner.displayName} has won the Tournament!")
        } else {
            Msg.world(Msg.RED, icon = 22, msg = "Tournament was ended, not enough players.")
        }
        destroyArea()
        TournamentScheduler.endTournament()
    }
    fun getFirstSpawn(): WorldTile = getTranslatedTile(PvPAreaType.FORSAKEN_QUARRY.firstSpawn!!)
    fun getSecondSpawn(): WorldTile = getTranslatedTile(PvPAreaType.FORSAKEN_QUARRY.secondSpawn!!)
    fun getLobby1(): WorldTile = getTranslatedTile(PvPAreaType.FORSAKEN_QUARRY.lobby1Tile!!)
    fun getLobby2(): WorldTile = getTranslatedTile(PvPAreaType.FORSAKEN_QUARRY.lobby2Tile!!)

    fun getTranslatedTile(original: WorldTile): WorldTile {
        val area = PvPAreaType.FORSAKEN_QUARRY
        val offsetX = (original.x shr 3) - area.southWestTile.chunkX
        val offsetY = (original.y shr 3) - area.southWestTile.chunkY

        return WorldTile(
            (boundChunks!![0] + offsetX) shl 3 or (original.x and 7),
            (boundChunks!![1] + offsetY) shl 3 or (original.y and 7),
            original.plane
        )
    }

    private fun shouldRemove(obj: WorldObject): Boolean {
        return when (obj.id) {
            // Rock piles, rubble, bushes
            83, 14384, 14385, 14386, 14388, 14389, 28195, 28177, 38806, 38807 -> true

            else -> false // keep everything else
        }
    }

    private fun debugTile(tile: WorldTile) {
        for (type in 0..22) {
            World.getObjectWithType(tile, type)?.let { obj ->
                println(
                    "Object @ $tile -> id=${obj.id}, type=${obj.type}, rotation=${obj.rotation}, " +
                            "x=${obj.x}, y=${obj.y}, plane=${obj.plane}"
                )
            }
        }
    }

    fun clearClutter() {
        val area = PvPAreaType.FORSAKEN_QUARRY

        val instSouthWest = getTranslatedTile(area.southWestTile)
        val instNorthEast = getTranslatedTile(area.northEastTile)

        for (x in instSouthWest.x..instNorthEast.x) {
            for (y in instSouthWest.y..instNorthEast.y) {
                for (z in 0..3) {
                    val tile = WorldTile(x, y, z)

                    World.getStandardFloorObject(tile)?.let { obj ->
                        if (shouldRemove(obj)) {
                            World.removeObject(obj)
                        }
                    }
                    World.getObjectWithType(tile, 10)?.let { obj ->
                        if (shouldRemove(obj)) {
                            World.removeObject(obj)
                        }
                    }
                    World.getStandardFloorDecoration(tile)?.let { obj ->
                        if (shouldRemove(obj)) {
                            World.removeObject(obj)
                        }
                    }
                }
            }
        }
    }

    private fun destroyArea() {
        val area = PvPAreaType.FORSAKEN_QUARRY
        val width = (area.northEastTile.x - area.southWestTile.x) / 8 + 1
        val height = (area.northEastTile.y - area.southWestTile.y) / 8 + 1
        MapBuilder.destroyMap(boundChunks!![0], boundChunks!![1], width, height)
    }

    fun getLobby(): TournamentLobby = lobby
}
