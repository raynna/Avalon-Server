package raynna.game.world.activity.pvpgame.lms

import raynna.game.World
import raynna.game.WorldTile
import raynna.game.player.AccountCreation
import raynna.game.player.Player
import raynna.game.player.actions.combat.Magic
import raynna.util.Utils
import raynna.game.world.activity.pvpgame.*

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
        player.appearance.generateAppearenceData()
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
