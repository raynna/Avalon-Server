package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.npc.combatdata.AggressivenessType

object MonsterExamineService {
    private const val COOLDOWN = 8250L
    private const val ATTR_KEY = "LAST_MONSTER_EXAMINE"

    fun cast(
        player: Player,
        target: Entity,
    ): Boolean {
        val npc = target as? NPC ?: return false

        if (!player.withinDistance(npc, 9)) {
            player.message("${npc.name} is too far away!")
            return false
        }

        val attrs = player.temporaryAttribute()

        val lastCast = attrs[ATTR_KEY] as? Long
        if (lastCast != null && lastCast + COOLDOWN > Utils.currentTimeMillis()) {
            return false
        }

        player.stopAll()

        player.faceEntity(npc)

        player.animate(Animation(6293))
        player.gfx(Graphics(1059))

        npc.gfx(Graphics(736, 0, 100))

        val interfaceId = if (player.interfaceManager.hasRezizableScreen()) 118 else 178
        player.interfaceManager.sendTab(interfaceId, 522)

        player.packets.sendTextOnComponent(522, 0, npc.definitions.name)
        player.packets.sendTextOnComponent(522, 1, "Combat level: ${if (npc.combatLevel == 0) "None" else npc.name}")
        player.packets.sendTextOnComponent(522, 2, "Life Points: ${npc.hitpoints}")
        player.packets.sendTextOnComponent(522, 3, "Max hit: ${npc.maxHit}")
        val aggressive = npc.combatDefinitions.aggressivenessType == AggressivenessType.AGGRESSIVE
        player.packets.sendTextOnComponent(522, 4, "Aggressive: ${if (aggressive) "Yes." else "No."}")

        attrs[ATTR_KEY] = Utils.currentTimeMillis()

        return true
    }
}
