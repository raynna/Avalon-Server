package com.rs.kotlin.game.world.activity.pvpgame

import com.rs.java.game.WorldTile
import com.rs.java.game.map.MapBuilder
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.Magic
import com.rs.java.utils.Utils

abstract class PvPGame {
    protected var boundChunks: IntArray? = null
    protected val players = mutableListOf<Player>()
    protected lateinit var area: PvPAreaType

    abstract fun start()
    abstract fun onPlayerDeath(player: Player)

    protected fun allocateArea(area: PvPAreaType) {
        this.area = area
        val width = (area.northEastTile.x - area.southWestTile.x) / 8 + 1
        val height = (area.northEastTile.y - area.southWestTile.y) / 8 + 1
        boundChunks = MapBuilder.findEmptyChunkBound(width, height)
        MapBuilder.copyAllPlanesMap(
            area.southWestTile.chunkX,
            area.southWestTile.chunkY,
            boundChunks!![0],
            boundChunks!![1],
            width,
            height
        )
    }

    fun cleanup(winner: Player?) {
        players.forEach {
            it.closePvPOverlay()
            if (winner != null) {
                it.showResult(winner)
            }
            Magic.teleport(it, WorldTile(2992, 9676, 0)) // Clan wars lobby
        }
        players.forEach { it.activePvPGame = null }
        destroyArea()
        PvPGameManager.unregisterGame(this)
    }

    protected fun destroyArea() {
        val width = (area.northEastTile.x - area.southWestTile.x) / 8 + 1
        val height = (area.northEastTile.y - area.southWestTile.y) / 8 + 1
        MapBuilder.destroyMap(boundChunks!![0], boundChunks!![1], width, height)
    }

    protected fun randomSpawn(): WorldTile {
        val x = (boundChunks!![0] shl 3) + Utils.random(63)
        val y = (boundChunks!![1] shl 3) + Utils.random(63)
        return WorldTile(x, y, 0)
    }
}