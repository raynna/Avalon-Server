import com.rs.Settings
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.kotlin.game.world.activity.pvpgame.PvPGameManager
import com.rs.kotlin.game.world.activity.pvpgame.activeLobby
import com.rs.kotlin.game.world.activity.pvpgame.activePvPGame
import com.rs.kotlin.game.world.activity.pvpgame.tournament.TournamentGame
import com.rs.kotlin.game.world.activity.pvpgame.tournament.TournamentInstance
import com.rs.kotlin.game.world.util.Msg

class TournamentLobby(private val instance: TournamentInstance) {
    private val waitingPlayers = mutableListOf<Player>()
    private val winners = mutableListOf<Player>()
    private val losers = mutableListOf<Player>()

    private var joinPhase = true
    private var ticksRemaining = 50 // 5 minutes in seconds (ticks)

    init {
        // Schedule end of join phase in 5 minutes

        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                ticksRemaining--

                when (ticksRemaining) {//180, 120, 60
                    180 -> Msg.world(Msg.GREEN, icon = 14,"<col=ff6600>News: Tournament starts in 3 minutes!")
                    120 -> Msg.world(Msg.GREEN, icon = 14,"<col=ff6600>News: Tournament starts in 2 minutes!")
                    60  -> Msg.world(Msg.GREEN, icon = 14,"<col=ff6600>News: Tournament starts in 1 minutes!")
                }
                println("Ticks remaining: $ticksRemaining")
                if (ticksRemaining <= 0) {
                    joinPhase = false
                    if (waitingPlayers.size >= 2) {
                        scheduleNextMatch(true)
                        stop()
                    } else {
                        val allPlayers = waitingPlayers + winners + losers
                        for (p in allPlayers) {
                            p.interfaceManager.closeOverlay(false)
                            p.activeLobby = null
                            p.activePvPGame = null
                            p.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION) // send them home
                            p.message("The tournament didn't have enough players to start, you've been sent back home.")
                        }
                        instance.end(null) // not enough players, cancel tournament
                        stop()
                    }
                }
            }
        }, 0, 0)
    }

    fun addPlayer(player: Player) {
        if (!joinPhase) {
            player.message("The tournament has already started, you can’t join now.")
            return
        }
        waitingPlayers.add(player)
        player.activeLobby = this

        // convert ticks → seconds
        val secondsRemaining = (ticksRemaining * 600) / 1000
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60

        val timeStr = when {
            minutes > 0 && seconds > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} and $seconds second${if (seconds > 1) "s" else ""}"
            minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""}"
            else -> "$seconds second${if (seconds > 1) "s" else ""}"
        }

        player.message("You joined the Tournament lobby. The first match begins in $timeStr!")
        refreshInterface(ticksRemaining)
    }



    private fun refreshInterface(nextFight: Int) {
        val allPlayers = waitingPlayers + winners + losers
        for (p in allPlayers) {
            if (p.hasFinished() || !p.hasStarted()) continue
            p.interfaceManager.sendOverlay(265, false)

            p.packets.sendTextOnComponent(265, 1, "Waiting:")
            p.packets.sendTextOnComponent(265, 2, "Waiting:")
            p.packets.sendTextOnComponent(265, 3, "Waiting:")
            p.packets.sendTextOnComponent(265, 9, "Winners:")

            p.packets.sendGlobalVar(
                261, waitingPlayers.size
            )
            p.packets.sendGlobalVar(
                262,
                winners.size
            )

            if (nextFight > 0) {
                p.packets.sendGlobalVar(270, nextFight) // ticks
            } else {
                p.packets.sendTextOnComponent(265, 16, "Fight active")
            }
        }
    }


    fun scheduleNextMatch(instant: Boolean) {
        WorldTasksManager.schedule(object : WorldTask() {
            var ticks = if (instant) 0 else 25//15 seconds
            override fun run() {
                refreshInterface(ticks)
                if (waitingPlayers.size < 2) {
                    stop(); return
                }
                if (--ticks <= 0) {
                    startMatch()
                    stop()
                }
            }
        }, 0, 0)
    }

    private fun startMatch() {
        if (waitingPlayers.size < 2) return
        val (p1, p2) = waitingPlayers.shuffled().take(2)
        waitingPlayers.removeAll(listOf(p1, p2))

        val game = TournamentGame(p1, p2, this)
        PvPGameManager.registerGame(game)
        game.start()
    }

    fun recordResult(winner: Player, loser: Player) {
        winners.add(winner)
        losers.add(loser)

        if (waitingPlayers.size >= 2) {
            scheduleNextMatch(false)
        } else if (waitingPlayers.isEmpty()) {
            // round finished
            if (winners.size == 1) {
                finishTournament(winners.first())
            } else {
                // next round
                waitingPlayers.addAll(winners)
                winners.clear()
                scheduleNextMatch(false)
            }
        }
    }

    fun finishTournament(champion: Player) {
        champion.message("Congratulations! You are the Tournament Champion!")

        val allPlayers = waitingPlayers + winners + losers
        for (p in allPlayers) {
            p.activeLobby = null
            p.activePvPGame = null
            p.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION) // send them home
            p.message("The tournament has ended. You’ve been sent back home.")
        }

        instance.end(champion)

        waitingPlayers.clear()
        winners.clear()
        losers.clear()
    }

    fun onLogin(player: Player) {
        if (!joinPhase && waitingPlayers.isEmpty() && winners.isEmpty() && losers.isEmpty()) {
            player.activeLobby = null
            player.activePvPGame = null
            player.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION)
            return
        }

        if (waitingPlayers.contains(player) || winners.contains(player) || losers.contains(player)) {
            player.activeLobby = this
        } else {
            player.activeLobby = null
            player.activePvPGame = null
            player.nextWorldTile = WorldTile(Settings.HOME_PLAYER_LOCATION)
        }
    }

    fun onLogout(player: Player) {
        waitingPlayers.remove(player)
        winners.remove(player)
        losers.remove(player)

        player.activeLobby = null
        player.activePvPGame = null
        player.setLocation(Settings.HOME_PLAYER_LOCATION) // force-save safe coords
    }



    fun getLobby1Tile() = instance.getLobby1()
    fun getLobby2Tile() = instance.getLobby2()
    fun getFirstSpawn() = instance.getFirstSpawn()
    fun getSecondSpawn() = instance.getSecondSpawn()
}
