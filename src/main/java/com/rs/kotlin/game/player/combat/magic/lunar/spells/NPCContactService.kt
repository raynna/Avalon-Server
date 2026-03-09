package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.minigames.duel.DuelArena
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.utils.Utils

object NPCContactService {
    private const val COOLDOWN = 15_000L
    private const val ATTR_KEY = "NPC_CONTACT"

    fun cast(player: Player): Boolean {
        if (player.isInCombat || player.controlerManager.controler is DuelArena) {
            player.packets.sendGameMessage("You can't npc contact right now.")
            return false
        }

        val attrs = player.temporaryAttribute()

        val lastCast = attrs[ATTR_KEY] as? Long
        if (lastCast != null && lastCast + COOLDOWN > Utils.currentTimeMillis()) {
            player.packets.sendGameMessage("You can only cast this spell every 15 seconds.")
            return false
        }

        player.stopAll()
        player.lock(3)
        attrs[ATTR_KEY] = Utils.currentTimeMillis()
        WorldTasksManager.schedule(3) {
            player.interfaceManager.sendInterface(88)
        }
        return true
    }
}
