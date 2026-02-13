package com.rs.kotlin.game.player.combat.magic.special

import com.rs.java.game.Graphics
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.magic.WeaponSpellRegistry
import com.rs.kotlin.game.player.combat.special.CombatContext
import com.rs.kotlin.game.player.combat.special.addHit
import com.rs.kotlin.game.player.combat.special.hits
import kotlin.math.floor
import kotlin.math.min

object NightmareStaff : WeaponSpellRegistry.Provider {

    override val weaponIds = setOf(22498, 22494)

    override fun hasWeapon(player: Player): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false
        if (!weapon.isAnyOf("item.nightmare_staff", "item.volatile_nightmare_staff", "item.eldritch_nightmare_staff", "item.harmonised_nightmare_staff")) {
            return false;
        }
        return true
    }

    fun special(context: CombatContext) {
        val attacker = context.attacker
        val defender = context.defender

        attacker.animate(15448)

        val base = calculateImmolateMaxHit(attacker) * 10

        val hit = context
            .addHit(CombatType.MAGIC)
            .baseDamage(base)
            .maxHit(820)
            .roll()

        context.hits {
            hit.graphic = if (hit.damage > 0) {
                Graphics(78)
            } else {
                Graphics(85)
            }
            addHit(defender, hit, hit.look, context.combat.getHitDelay())
        }
    }


    /**
     * Max hit formula for volatile staff special (Immolate).
     */
    private fun calculateImmolateMaxHit(player: Player): Int {
        val level = player.skills.getLevel(Skills.MAGIC).coerceAtMost(player.skills.getLevelForXp(Skills.MAGIC))
        val spellMax = min(floor(58.0 * (level / 99.0 + 1.0)).toInt(), 58)
        return floor(spellMax.toDouble()).toInt()
    }
}
