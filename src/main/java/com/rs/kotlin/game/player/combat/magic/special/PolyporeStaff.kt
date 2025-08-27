package com.rs.kotlin.game.player.combat.magic.special

import com.rs.java.game.Entity
import com.rs.java.game.Graphics
import com.rs.java.game.item.Item
import com.rs.java.game.item.meta.PolyporeStaffMetaData
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.damage.PendingHit
import com.rs.kotlin.game.player.combat.magic.MagicStyle
import com.rs.kotlin.game.player.combat.magic.WeaponSpellRegistry
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object PolyporeStaff : WeaponSpellRegistry.Provider {

    override val weaponIds = setOf(22498, 22494)

    override fun hasWeapon(player: Player): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false
        if (!weapon.isAnyOf("item.polypore_staff", "item.polypore_staff_degraded")) {
            return false;
        }
        if (weapon.metadata == null) {
            weapon.metadata = PolyporeStaffMetaData(3000)
        }
        val data = weapon.metadata
        if (data is PolyporeStaffMetaData && data.value < 1) {
            player.message("Your polypore staff has no charges.")
            return false
        }
        return true
    }

    fun cast(style: MagicStyle, attacker: Player, defender: Entity) {
        val weapon = attacker.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return
        val data = weapon.metadata as? PolyporeStaffMetaData ?: return
        if (data.value <= 0) {
            attacker.message("Your polypore staff has no charges.")
            return
        }
        if (weapon.isItem("item.polypore_staff")) {
            weapon.id = Item.getId("item.polypore_staff_degraded")
            attacker.equipment.refresh()
            attacker.appearence.generateAppearenceData()
        }

        // consume a charge
        data.value -= 1
        if (data.value == 0) {
            attacker.message("Your polypore staff has run out of charges.")
        }

        attacker.animate(15448)
        attacker.gfx(Graphics(2034))

        val hit = style.registerHit(attacker, defender, CombatType.MAGIC, spellId = 1000)
        val splash = hit.damage == 0
        val endGfx = if (!splash) Graphics(2036, 100) else Graphics(85, 100)

        ProjectileManager.send(
            projectile = Projectile.POLYPORE_STAFF,
            gfxId = 2035,
            attacker = attacker,
            defender = defender,
            hitGraphic = endGfx
        )

        style.delayHits(PendingHit(hit, defender, style.getHitDelay()))
    }
}
