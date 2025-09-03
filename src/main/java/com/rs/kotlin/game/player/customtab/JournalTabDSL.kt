// app/tabs/JournalTabDSL.kt
package com.rs.kotlin.game.player.customtab

import com.rs.Settings
import com.rs.java.game.ForceTalk
import com.rs.java.game.World
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks.Rank
import com.rs.java.utils.HexColours.Colour
import java.text.DecimalFormat

object JournalTabDSL {

    private const val KEY = "journal"
    private const val IFACE = 3002
    private const val BACK = 58
    private const val FWD = 27
    private const val BLUE = 62

    private val chrome = TabChromeIds(
        ifaceId = IFACE,
        firstSlot = 3, lastSlot = 22,
        backButton = BACK, forwardButton = FWD,
        blueStar = BLUE, greenStar = 61, redStar = 60, purpleStar = 59, yellowStar = 26,
        blueHighlighted = 12184, greenHighlighted = 12182, redHighlighted = 12186, purpleHighlighted = 12185, yellowHighlighted = 12187
    )

    fun open(player: Player) {
        player.temporaryAttributtes["CUSTOMTAB"] = 0
        TabRuntime.open(player, buildPage())
    }

    fun handle(player: Player, compId: Int): Boolean =
        TabRuntime.handleButton(player, buildPage(), compId)

    private fun buildPage(): TabPage = tabPage(KEY, chrome, "Journal") {
        onBack = null
        onForward = { p -> TeleportTabDSL.open(p) }

        onBeforeOpen { p ->
            val pk = p.packets
            pk.sendHideIComponent(IFACE, BACK, true)
            pk.sendHideIComponent(IFACE, FWD, false)
            pk.sendSpriteOnIComponent(IFACE, BLUE, chrome.blueHighlighted)
        }

        // SERVER INFO
        label { "<u>Server Information" }
        action({ "Players Online: <col=04BB3B>${World.players.size}" }) { p ->
            p.sendPlayersList()
            p.packets.sendGameMessage("Players online: ${World.players.size}.")
        }
        action({ "Players in Wilderness: <col=04BB3B>${World.getPlayersInWilderness()}" }) { p ->
            p.packets.sendGameMessage("Players in the Wilderness: ${World.getPlayersInWilderness()}.")
        }
        action({ "Players in Edgeville PvP: <col=04BB3B>${World.getPlayersInPVP()}" }) { p ->
            p.packets.sendGameMessage("Players in Edgeville PvP: ${World.getPlayersInPVP()}.")
        }
        action({ "Clan Wars (FFA): <col=04BB3B>${World.getPlayersInFFA()}" }) { p ->
            p.packets.sendGameMessage("Players in Clan Wars (FFA): ${World.getPlayersInFFA()}.")
        }
        action({ "Double Drops: " + if (Settings.DOUBLE_DROP) "<col=04BB3B>Active" else "<col=BB0404>Inactive" }) { p ->
            p.packets.sendGameMessage("Double drops is ${if (Settings.DOUBLE_DROP) "activated." else "inactivated."}")
        }

        spacer()

        // PLAYER INFO
        label { "<u>Player Information" }
        action({
            val rank = it.getPlayerRank()
            val icon = when {
                rank.rank[0] == Rank.DEVELOPER -> it.messageIcon - 1
                rank.isHardcore() -> 24
                rank.isIronman() -> 23
                else -> it.messageIcon
            }
            "Rank: ${Colour.GREEN.hex}<img=$icon>${rank.getRankName(0)}"
        }) { p ->
            p.packets.sendGameMessage("My ranks is: <img=${p.messageIcon}>${p.playerRank.rankNames}")
            p.setNextForceTalk(ForceTalk("My ranks is: <img=${p.messageIcon}>${p.playerRank.rankNames}"))
        }
        action({
            val donator = it.playerRank.isDonator
            val text = if (donator) "<img=${it.donatorIcon}>${it.playerRank.getRankName(1)}"
                       else "${Colour.RED.hex}I'm not a donator"
            "Donator rank: ${Colour.WHITE.hex}$text"
        }) { p ->
            val donator = p.playerRank.isDonator
            val msg = if (donator) "<img=${p.donatorIcon}>${p.playerRank.getRankName(1)}" else "I'm not a donator"
            p.packets.sendGameMessage("My Donator rank is: $msg")
            p.setNextForceTalk(ForceTalk("My Donator rank is: $msg"))
        }
        action({
            "Title: " + if (it.appearence.title != -1 && it.customTitle == null) {
                it.appearence.titleString
            } else {
                "<col=BB0404>None - click to set"
            }
        }) { p ->
            p.setCustomTitle(null)
            p.temporaryAttributtes["SET_TITLE"] = true
            p.packets.sendRunScript(108, arrayOf("Enter title id, 0-58, 0 = none:"))
        }

        // Kill/death/ratio
        action({ "Kills: <col=04BB3B>${it.killCount}" }) { p ->
            p.packets.sendGameMessage("My killcount is: ${p.killCount}.")
            p.setNextForceTalk(ForceTalk("My killcount is: ${p.killCount}."))
        }
        action({ "Deaths: <col=04BB3B>${it.deathCount}" }) { p ->
            p.packets.sendGameMessage("My deathcount is: ${p.deathCount}.")
            p.setNextForceTalk(ForceTalk("My deathcount is: ${p.deathCount}."))
        }
        action({
            val dr = if (it.deathCount == 0) it.killCount.toDouble() else it.killCount.toDouble() / it.deathCount
            "K/D Ratio: <col=04BB3B>${DecimalFormat("0.00").format(dr)}"
        }) { p ->
            val dr = if (p.deathCount == 0) p.killCount.toDouble() else p.killCount.toDouble() / p.deathCount
            val f = DecimalFormat("0.00").format(dr)
            p.packets.sendGameMessage("My kill/death ratio is: $f.")
            p.setNextForceTalk(ForceTalk("My kill/death ratio is: $f."))
        }

        // XP, EP
        action({
            "Bonus Experience: " + if (it.bonusExp > 1) "<col=04BB3B>${it.bonusExp}x" else "<col=BB0404>${it.bonusExp}x"
        }) { p ->
            p.packets.sendGameMessage("Your bonus experience is ${p.bonusExp}.")
            p.setNextForceTalk(ForceTalk("My bonus experience multiplier is ${p.bonusExp}."))
        }
        action({
            val col = when {
                it.ep == 100 -> "<col=04BB3B>"
                it.ep > 0 -> "<col=FFF300>"
                else -> "<col=BB0404>"
            }
            "Ep: $col${it.ep}%"
        }) { p ->
            p.packets.sendGameMessage("Your Ep is: ${p.ep}.")
            p.setNextForceTalk(ForceTalk("I have a total of ${p.ep}% EP."))
        }

        // Slayer
        action({
            "<br>Slayer Task: <col=04BB3B>" +
                (it.slayerTask ?: "I don't have a task.")
        }) { p ->
            val msg = p.slayerTask?.let { "I have $it to hunt." } ?: "I don't have a slayer task."
            p.packets.sendGameMessage(msg)
            p.nextForceTalk = ForceTalk(msg)
        }
        action({ player ->
            player.slayerTaskTip?.let {
                "<u><br><br><br>Locations:<br><col=04BB3B>" +
                    it.replace(" and ", "<br><col=04BB3B>")
                      .replace(", ", "<br><col=04BB3B>")
                      .replace(".", "")
            } ?: ""
        }) { p ->
            p.slayerTaskTip?.let {
                p.packets.sendGameMessage("You can find your slayer monsters in:<br>$it")
            }
        }
    }
}
