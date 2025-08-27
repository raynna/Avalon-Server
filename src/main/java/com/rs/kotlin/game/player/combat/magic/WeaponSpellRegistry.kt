package com.rs.kotlin.game.player.combat.magic

import com.rs.java.game.player.Player

object WeaponSpellRegistry {

    interface Provider {
        val weaponIds: Set<Int>
        fun hasWeapon(player: Player): Boolean?
    }
}
