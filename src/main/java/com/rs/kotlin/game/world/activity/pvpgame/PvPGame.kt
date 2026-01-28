package com.rs.kotlin.game.world.activity.pvpgame

import com.rs.java.game.WorldTile
import com.rs.java.game.map.MapBuilder
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.Magic
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.activity.pvpgame.tournament.TournamentGame

abstract class PvPGame {
    protected var boundChunks: IntArray? = null
    protected val players = mutableListOf<Player>()
    protected var area: PvPAreaType? = null   // <-- was lateinit

    abstract fun start()
    abstract fun onPlayerDeath(player: Player)

    protected open fun shouldDestroyArea(): Boolean = true

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

            if (winner != null) it.showResult(winner)

            // default behavior for most games
            Magic.teleport(it, WorldTile(2992, 9676, 0))
            it.activePvPGame = null
        }

        if (shouldDestroyArea()) {
            destroyArea()
        }

        PvPGameManager.unregisterGame(this)
    }

    protected fun destroyArea() {
        val a = area ?: return
        val chunks = boundChunks ?: return

        val width = (a.northEastTile.x - a.southWestTile.x) / 8 + 1
        val height = (a.northEastTile.y - a.southWestTile.y) / 8 + 1
        MapBuilder.destroyMap(chunks[0], chunks[1], width, height)
    }

    protected fun randomSpawn(): WorldTile {
        val x = (boundChunks!![0] shl 3) + Utils.random(63)
        val y = (boundChunks!![1] shl 3) + Utils.random(63)
        return WorldTile(x, y, 0)
    }
}
