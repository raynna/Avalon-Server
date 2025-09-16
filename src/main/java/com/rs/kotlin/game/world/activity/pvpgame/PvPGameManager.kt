package com.rs.kotlin.game.world.activity.pvpgame

import TournamentLobby
import com.rs.java.game.player.Player
import com.rs.kotlin.game.world.activity.pvpgame.lms.LastManStandingLobby

object PvPGameManager {
    private val activeGames = mutableListOf<PvPGame>()
    private var tournamentLobby: TournamentLobby? = null
    private var lmsLobby: LastManStandingLobby? = null

    fun joinLMS(player: Player) {
        if (lmsLobby == null) lmsLobby = LastManStandingLobby()
        lmsLobby!!.addPlayer(player)
    }

    fun registerGame(game: PvPGame) {
        activeGames.add(game)
    }

    fun unregisterGame(game: PvPGame) {
        activeGames.remove(game)
    }
}


var Player.activePvPGame: PvPGame?
    get() = temporaryAttribute()["active_pvp_game"] as? PvPGame
    set(value) {
        if (value == null) {
            temporaryAttribute().remove("active_pvp_game")
        } else {
            temporaryAttribute()["active_pvp_game"] = value
        }
    }

var Player.activeLobby: TournamentLobby?
    get() = temporaryAttribute()["active_pvp_lobby"] as? TournamentLobby
    set(value) {
        if (value == null) {
            temporaryAttribute().remove("active_pvp_lobby")
        } else {
            temporaryAttribute()["active_pvp_lobby"] = value
        }
    }


fun Player.openPvPOverlay(player: Player, target: Player) {
    interfaceManager.sendTab(if (interfaceManager.hasRezizableScreen())  11 else 29, 265)
    packets.sendTextOnComponent(265, 3, "You:")
    packets.sendTextOnComponent(265, 5, player.displayName)
    packets.sendHideIComponent(265, 4, true)
    packets.sendHideIComponent(265, 10, true)
    packets.sendTextOnComponent(265, 9, "Opponent:")
    packets.sendTextOnComponent(265, 11, target.displayName)
}

fun Player.closePvPOverlay() {
    val resizable = interfaceManager.hasRezizableScreen()
    packets.closeInterface(if (resizable) 746 else 548, if (resizable) 11 else 29)
}

fun Player.showResult(winner: Player?) {
    interfaceManager.sendInterface(790)
    if (winner != null) {
        packets.sendGlobalVar(268, 4)
        packets.sendTextOnComponent(790, 4, "Winner is: " + winner.displayName)
        packets.sendTextOnComponent(790, 5, "You won in total of " + 1 + " fights this tournament.")
        packets.sendHideIComponent(790, 6, true)
    } else {
        packets.sendGlobalVar(268, 8)
        packets.sendTextOnComponent(790, 4, "You lost the fight.")
        packets.sendTextOnComponent(790, 5, "You won in total of " + 0 + " fights this tournament.")
        packets.sendHideIComponent(790, 6, true)
    }
}

