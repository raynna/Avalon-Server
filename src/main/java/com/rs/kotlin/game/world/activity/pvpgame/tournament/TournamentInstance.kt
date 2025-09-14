package com.rs.kotlin.game.world.activity.pvpgame.tournament

import TournamentLobby
import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.map.MapBuilder
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.Magic
import com.rs.kotlin.game.world.activity.pvpgame.PvPAreaType
import com.rs.kotlin.game.world.util.Msg

class TournamentInstance {
    private val lobby = TournamentLobby(this)
    private var boundChunks: IntArray? = null

    fun create() {
        // Allocate Forsaken Quarry arena
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
    }

    fun end(winner: Player?) {
        if (winner != null) {
            Msg.world(Msg.ORANGE, "${winner.displayName} has won the Tournament!")
        } else {
            Msg.world(Msg.RED, icon = 14, msg = "Tournament was ended, not enough players.")
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



    private fun destroyArea() {
        val area = PvPAreaType.FORSAKEN_QUARRY
        val width = (area.northEastTile.x - area.southWestTile.x) / 8 + 1
        val height = (area.northEastTile.y - area.southWestTile.y) / 8 + 1
        MapBuilder.destroyMap(boundChunks!![0], boundChunks!![1], width, height)
    }

    fun getLobby(): TournamentLobby = lobby
}
