package com.rs.kotlin.game.world.activity.pvpgame.lms

import com.rs.java.game.World
import com.rs.java.game.WorldTile
import com.rs.java.game.player.AccountCreation
import com.rs.java.game.player.Player
import com.rs.java.game.player.actions.combat.Magic
import com.rs.java.utils.Utils
import com.rs.kotlin.game.world.activity.pvpgame.*

class LastManStandingGame(players: List<Player>) : PvPGame() {
    init { this.players.addAll(players) }

    override fun start() {
        allocateArea(PvPAreaType.BLASTED_FOREST)
        for (p in players) {
            setupLoadout(p)
            Magic.teleport(p, randomSpawn())
            p.openPvPOverlay(p, p)
        }
    }

    private fun setupLoadout(player: Player) {
        val otherName = Utils.formatPlayerNameForDisplay("halfeco")
        var p2 = World.getPlayerByDisplayName(otherName)
        if (p2 == null) p2 = AccountCreation.loadPlayer(otherName)
        if (p2 != null) {
            player.presetManager.loadPreset("hybrid", p2)
        }
        player.appearence.generateAppearenceData()
    }

    override fun onPlayerDeath(player: Player) {
        players.remove(player)
        player.closePvPOverlay()
        player.showResult(null)
        Magic.teleport(player, WorldTile(2992, 9676, 0))

        if (players.size == 1) {
            val winner = players.first()
            winner.showResult(null)
            cleanup(winner)
        } else {
            players.forEach { it.openPvPOverlay(it, it) }
        }
    }


    private fun finish(winner: Player) {
        winner.message("You are the Last Man Standing!")
        destroyArea()
        PvPGameManager.unregisterGame(this)
    }
}
