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

        if (weapon.isItem("item.polypore_staff")) {
            return true
        }

        if (weapon.isItem("item.polypore_staff_degraded")) {
            val data = weapon.metadata as? PolyporeStaffMetaData ?: return false
            return data.value > 0
        }
        return false
    }


    fun cast(style: MagicStyle, attacker: Player, defender: Entity) {
        var weapon = attacker.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return

        // Fresh staff first use
        if (weapon.isItem("item.polypore_staff")) {
            val degraded = Item(
                Item.getId("item.polypore_staff_degraded"),
                weapon.amount,
                PolyporeStaffMetaData(PolyporeStaffMetaData.MAX_CHARGES)
            )
            attacker.equipment.updateItemWithMeta(
                Equipment.SLOT_WEAPON.toInt(),
                degraded
            )
            attacker.equipment.refresh()
            attacker.appearance.generateAppearenceData()
            weapon = attacker.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return

        }

        val data = weapon.metadata as? PolyporeStaffMetaData ?: return

        if (data.value <= 0) {
            attacker.message("Your polypore staff has no charges.")
            return
        }

        data.decrement(1)

        if (data.value == 0) {
            attacker.message("Your polypore staff has run out of charges.")
            attacker.equipment.updateItem(
                Equipment.SLOT_WEAPON.toInt(),
                Item.getId("item.polypore_stick")
            )
            return
        }


        attacker.animate(15448)
        attacker.gfx(Graphics(2034))
        val hit = style.registerHit(attacker, defender, CombatType.MAGIC, spellId = 1000)
        val splash = hit.damage == 0
        val endGfx = if (!splash) Graphics(2036, 100) else Graphics(85, 100)

        val impactTicks = ProjectileManager.send(
            projectile = Projectile.POLYPORE_STAFF,
            gfxId = 2035,
            attacker = attacker,
            defender = defender,
            hitGraphic = endGfx
        )

        style.delayHits(PendingHit(hit, defender, impactTicks))
        if (data.value == 0) {
            attacker.message("Your polypore staff has run out of charges.")
            attacker.equipment.updateItem(Equipment.SLOT_WEAPON.toInt(), Item.getId("item.polypore_stick"))
            attacker.equipment.refresh()
            attacker.appearance.generateAppearenceData()
        }
    }
}
