package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils

object StatSpyService {
    private const val COOLDOWN = 8250L
    private const val ATTR_KEY = "LAST_SPELL"

    fun cast(
        player: Player,
        target: Entity?,
    ): Boolean {
        val other = target as? Player ?: return false

        player.faceEntity(other)

        if (!player.withinDistance(other, 9)) {
            player.message("${other.displayName} is too far away!")
            return false
        }

        val lastCast = player.temporaryAttribute()[ATTR_KEY] as? Long
        if (lastCast != null && lastCast + COOLDOWN > Utils.currentTimeMillis()) {
            return false
        }

        player.stopAll()

        player.animate(Animation(6293))
        player.gfx(Graphics(1059))
        other.gfx(Graphics(736, 0, 100))

        val iface = if (player.interfaceManager.hasRezizableScreen()) 114 else 174

        player.interfaceManager.sendTab(iface, 523)
        player.interfaceManager.openGameTab(3)

        player.packets.sendTextOnComponent(
            523,
            103,
            "Viewing stats for <br><img=${other.rights}>${other.displayName}",
        )

        var start = 1
        var end = 2

        for (i in 0 until 25) {
            player.packets.sendTextOnComponent(
                523,
                start,
                other.skills.getLevel(i).toString(),
            )

            player.packets.sendTextOnComponent(
                523,
                end,
                "99",
            )

            start += 4
            end += 4
        }

        player.packets.sendTextOnComponent(523, 98, "120")
        player.packets.sendTextOnComponent(523, 106, "Hitpoints: ${other.hitpoints}")

        player.temporaryAttribute()[ATTR_KEY] = Utils.currentTimeMillis()

        other.message("Your stats are being spied on by ${player.displayName}.")

        return true
    }
}
