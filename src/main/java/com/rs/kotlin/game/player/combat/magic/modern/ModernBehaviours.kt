package com.rs.kotlin.game.player.combat.magic.modern

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.World
import com.rs.java.game.item.ground.GroundItems
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.actions.skills.crafting.Enchanting
import com.rs.kotlin.game.player.combat.magic.SpellBehaviour
import com.rs.kotlin.game.player.combat.magic.SpellHandler
import com.rs.kotlin.game.player.combat.magic.SpellType
import com.rs.kotlin.game.player.combat.magic.modern.spells.AlchemyService
import com.rs.kotlin.game.player.combat.magic.modern.spells.BonesToService
import com.rs.kotlin.game.player.combat.magic.modern.spells.SuperHeatService
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object ModernBehaviours {
    val chargeOrb =
        SpellBehaviour { player, spell ->

            val obj = SpellHandler.getTargetObject(player) ?: return@SpellBehaviour false

            val type = spell.type as? SpellType.ObjectSpecific ?: return@SpellBehaviour false

            val orbItemId = type.orbItemId ?: return@SpellBehaviour false

            if (obj.id !in type.objectIds) {
                player.message("Nothing interesting happens.")
                return@SpellBehaviour false
            }

            if (!player.inventory.containsItem("item.unpowered_orb")) {
                player.message("You need an unpowered orb to charge.")
                return@SpellBehaviour false
            }

            player.dialogueManager.startDialogue(
                "ChargeOrbD",
                spell,
                orbItemId,
            )
            true
        }

    val bonesToBananas =
        SpellBehaviour { player, spell ->
            BonesToService.cast(player, false)
        }

    val bonesToPeaches =
        SpellBehaviour { player, spell ->
            BonesToService.cast(player, true)
        }

    val charge =
        SpellBehaviour { player, _ ->
            player.animate(811)
            player.tickManager.addMinutes(TickManager.TickKeys.CHARGE_SPELL, 2)
            player.message("You are now feeling the power of the charge spell.")
            true
        }

    val enchantCrossbowBolt =
        SpellBehaviour { player, _ ->
            player.stopAll()
            player.interfaceManager.sendInterface(432)
            true
        }

    val lowAlchemy =
        SpellBehaviour { player, spell ->
            val itemId = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            val slotId = SpellHandler.getTargetSlotId(player) ?: return@SpellBehaviour false
            val fireStaff = player.equipment.weaponId in listOf(1387, 1393, 1401, 3053, 3054)
            AlchemyService.cast(player, itemId, slotId, fireStaff, true)
        }

    val highAlchemy =
        SpellBehaviour { player, spell ->
            val itemId = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            val slotId = SpellHandler.getTargetSlotId(player) ?: return@SpellBehaviour false
            val fireStaff = player.equipment.weaponId in listOf(1387, 1393, 1401, 3053, 3054)
            AlchemyService.cast(player, itemId, slotId, fireStaff, false)
        }

    val superheat =
        SpellBehaviour { player, spell ->
            val itemId = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            val slotId = SpellHandler.getTargetSlotId(player) ?: return@SpellBehaviour false
            SuperHeatService.cast(player, itemId, slotId)
        }

    fun enchantJewellery(level: Int) =
        SpellBehaviour { player, spell ->
            val itemId = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            val slotId = SpellHandler.getTargetSlotId(player) ?: return@SpellBehaviour false
            Enchanting.startEnchant(player, itemId, slotId, level, spell.xp)
        }

    val telegrab =
        SpellBehaviour { player, spell ->
            val floorItem = SpellHandler.getTargetFloorItem(player) ?: return@SpellBehaviour false
            val tile = floorItem.tile
            val itemId = floorItem.id
            if (!player.inventory.canHold(floorItem)) {
                player.message("You don't have enough inventory space.")
                return@SpellBehaviour false
            }
            player.faceWorldTile(tile)
            player.animate(Animation(711))
            player.gfx(142, 50)

            ProjectileManager.sendToTile(Projectile.TELEGRAB, 143, player, tile) {
                val region = World.getRegion(tile.regionId)
                val currentItem = region.getGroundItem(itemId, tile, player)

                if (currentItem == null || currentItem.isRemoved) {
                    player.message("Too late!")
                    return@sendToTile
                }

                World.sendGraphics(player, Graphics(144), tile)
                GroundItems.removeGroundItem(player, currentItem)
                player.skills.addXp(Skills.MAGIC, spell.xp)
            }
            true
        }
}
