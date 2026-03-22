package raynna.game.player.combat.magic

import raynna.game.player.Player

object WeaponSpellRegistry {

    interface Provider {
        val weaponIds: Set<Int>
        fun hasWeapon(player: Player): Boolean?
    }
}
