@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.rs.kotlin.game.player.combat.magic

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.*
import com.rs.java.game.item.FloorItem
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData
import com.rs.java.game.minigames.clanwars.FfaZone
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.controllers.CrucibleController
import com.rs.java.game.player.controllers.EdgevillePvPController
import com.rs.java.game.player.controllers.FightCaves
import com.rs.java.game.player.controllers.FightKiln
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatAction
import com.rs.kotlin.game.player.combat.magic.ancient.AncientMagicks
import com.rs.kotlin.game.player.combat.magic.dungeoneering.DungeoneeringMagicks
import com.rs.kotlin.game.player.combat.magic.lunar.LunarMagicks
import com.rs.kotlin.game.player.combat.magic.lunar.spells.SpellbookSwapService
import com.rs.kotlin.game.player.combat.magic.modern.ModernMagicks

object SpellHandler {
    private const val ATTR_TARGET = "spell_target"
    private const val ATTR_ITEM_ID = "spell_itemid"
    private const val ATTR_SLOT_ID = "spell_slotid"
    private const val ATTR_OBJECT = "spell_object"
    private const val ATTR_FLOOR_ITEM = "spell_flooritem"
    private const val ATTR_TEMP_CAST_SPELL = "tempCastSpell"

    @JvmStatic
    fun selectCombatSpell(
        player: Player,
        spellId: Int,
    ) {
        player.message("spellId: " + spellId)
        val spell = getSpellForPlayer(player, spellId) ?: return

        if (spell.type !is SpellType.Combat) {
            cast(player, spellId, false)
            return
        }

        if (!canCast(player, spell) || !checkAndRemoveRunes(player, spell)) {
            return
        }

        if (player.combatDefinitions.autoCastSpell == spell.id) {
            player.combatDefinitions.resetSpells(true)
        } else {
            if (player.combatDefinitions.autoCastSpell > 0) {
                player.combatDefinitions.resetSpells(true)
            }
            player.combatDefinitions.setAutoCastSpell(spell.id)
        }
    }

    fun cast(
        player: Player,
        spellId: Int,
        autocast: Boolean = false,
    ) {
        val spell = getSpellForPlayer(player, spellId) ?: return
        if (!canCast(player, spell)) return

        when (spell.type) {
            SpellType.Combat -> handleCombatSpell(player, spell, autocast)
            SpellType.Teleport -> handleTeleportSpell(player, spell)
            is SpellType.ObjectSpecific -> handleObjectSpecificSpell(player, spell)
            else -> executeBehaviourSpell(player, spell)
        }
    }

    private fun executeBehaviourSpell(
        player: Player,
        spell: Spell,
    ) {
        val behaviour =
            spell.behaviour ?: run {
                player.message("Unhandled spell: ${spell.name}")
                return
            }
        if (!RuneService.hasRunes(player, spell.runes)) return
        val success = behaviour.cast(player, spell)
        if (success) {
            RuneService.consumeRunes(player, spell.runes)
            player.skills.addXp(Skills.MAGIC, spell.xp)
            SpellbookSwapService.consumeSwap(player)
        }
        clearSpellTargets(player)
    }

    private fun handleCombatSpell(
        player: Player,
        spell: Spell,
        autocast: Boolean,
    ) {
        if (!autocast) {
            player.temporaryAttribute()[ATTR_TEMP_CAST_SPELL] = spell.id
        }

        val target =
            player.temporaryAttribute()[ATTR_TARGET] as? Entity ?: run {
                player.message("Invalid spell target.")
                return
            }

        player.actionManager.setAction(CombatAction(target))
    }

    private fun handleTeleportSpell(
        player: Player,
        spell: Spell,
    ) {
        val location = spell.teleportLocation ?: return
        sendTeleportSpell(
            player,
            if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 9599 else 8939,
            if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 1681 else 1576,
            spell,
            location,
        )
    }

    private fun handleObjectSpecificSpell(
        player: Player,
        spell: Spell,
    ) {
        val worldObject = player.temporaryAttribute()[ATTR_OBJECT] as? WorldObject ?: return
        val type = spell.type as? SpellType.ObjectSpecific ?: return

        if (worldObject.id !in type.objectIds) {
            player.message("Nothing interesting happens.")
            return
        }

        if (!checkAndRemoveRunes(player, spell)) return

        try {
            spell.behaviour?.cast(player, spell)
                ?: player.message("Unhandled spell: ${spell.name}")
        } finally {
            clearSpellTargets(player)
        }
    }

    fun castOnItem(
        player: Player,
        spellId: Int,
        itemId: Int,
        slotId: Int,
    ) {
        player.temporaryAttribute()[ATTR_ITEM_ID] = itemId
        player.temporaryAttribute()[ATTR_SLOT_ID] = slotId
        cast(player, spellId)
    }

    fun castOnObject(
        player: Player,
        spellId: Int,
        obj: WorldObject,
    ) {
        player.temporaryAttribute()[ATTR_OBJECT] = obj
        cast(player, spellId)
    }

    fun castOnFloorItem(
        player: Player,
        spellId: Int,
        floorItem: FloorItem,
    ) {
        player.temporaryAttribute()[ATTR_FLOOR_ITEM] = floorItem
        cast(player, spellId)
    }

    @JvmStatic
    fun castOnPlayer(
        player: Player,
        spellId: Int,
        target: Player,
    ) {
        player.temporaryAttribute()[ATTR_TARGET] = target
        cast(player, spellId)
    }

    @JvmStatic
    fun castOnNpc(
        player: Player,
        spellId: Int,
        target: NPC,
    ) {
        player.temporaryAttribute()[ATTR_TARGET] = target
        cast(player, spellId)
    }

    fun getSpellForPlayer(
        player: Player,
        spellId: Int,
    ): Spell? =
        when (player.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            DungeoneeringMagicks.id -> DungeoneeringMagicks.getSpell(spellId)
            LunarMagicks.id -> LunarMagicks.getSpell(spellId)
            else -> null
        }

    fun canCast(
        player: Player,
        spell: Spell,
    ): Boolean {
        if (spell.type is SpellType.Combat) {
            if (player.skills.getLevelForXp(Skills.MAGIC) < spell.level) {
                player.packets.sendGameMessage("You need at least level ${spell.level} Magic to cast this spell.")
                return false
            }
        } else {
            if (player.skills.getLevel(Skills.MAGIC) < spell.level) {
                player.packets.sendGameMessage("You need at least level ${spell.level} Magic to cast this spell.")
                return false
            }
        }

        if (spell.type is SpellType.Instant) {
            if (spell.id == 83 && player.tickManager.isActive(TickManager.TickKeys.CHARGE_SPELL)) {
                player.message(
                    "Your charge is still active for another ${player.tickManager.getTimeLeft(TickManager.TickKeys.CHARGE_SPELL)}s.",
                )
                return false
            }
        }

        if (player.isLocked) {
            return false
        }

        spell.staff?.let { staffReq ->
            if (staffReq.anyOf.isNotEmpty() &&
                staffReq.anyOf.none { id -> player.equipment.weaponId == id }
            ) {
                player.packets.sendGameMessage("You don't have the required staff equipped to cast this spell.")
                return false
            }
        }

        spell.itemRequirement?.let { req ->
            if (req.anyOf.isNotEmpty() && req.anyOf.none { player.equipment.containsOneItem(it) }) {
                player.packets.sendGameMessage("You don't have the required item to cast this spell.")
                return false
            }

            if (req.allOf.isNotEmpty() && !req.allOf.all { player.equipment.containsOneItem(it) }) {
                player.packets.sendGameMessage("You don't have all required items to cast this spell.")
                return false
            }
        }

        return true
    }

    private fun staffOfLightEffect(player: Player): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false
        return when {
            weapon.isAnyOf("item.kodai_wand") -> Utils.roll(3, 20)

            weapon.isAnyOf(
                "item.staff_of_light",
                "item.staff_of_light_red",
                "item.staff_of_light_blue",
                "item.staff_of_light_green",
                "item.staff_of_light_gold",
            ) -> Utils.roll(1, 8)

            else -> false
        }
    }

    fun checkAndRemoveRunes(
        player: Player,
        spell: Spell,
    ): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt())

        if (weapon?.metadata is GreaterRunicStaffMetaData) {
            val data = weapon.metadata as GreaterRunicStaffMetaData
            if (spell.id == data.spellId && data.charges > 0) {
                data.removeCharges(1)
                return true
            }
        }

        if (!RuneService.hasRunes(player, spell.runes)) {
            player.combatDefinitions.resetSpells(true)
            return false
        }

        if (spell.type == SpellType.Combat && staffOfLightEffect(player)) {
            val isKodai = weapon?.isAnyOf("item.kodai_wand") ?: false
            player.packets.sendGameMessage(
                "Your spell draws its power completely from your ${if (isKodai) "wand" else "staff"}.",
            )
            return true
        }

        RuneService.consumeRunes(player, spell.runes)
        return true
    }

    fun getTarget(player: Player): Entity? = player.temporaryAttribute()[ATTR_TARGET] as? Entity

    fun getTargetPlayer(player: Player): Player? = player.temporaryAttribute()[ATTR_TARGET] as? Player

    fun getTargetNpc(player: Player): NPC? = player.temporaryAttribute()[ATTR_TARGET] as? NPC

    fun getTargetObject(player: Player): WorldObject? = player.temporaryAttribute()[ATTR_OBJECT] as? WorldObject

    fun getTargetFloorItem(player: Player): FloorItem? = player.temporaryAttribute()[ATTR_FLOOR_ITEM] as? FloorItem

    fun getTargetItemId(player: Player): Int? = player.temporaryAttribute()[ATTR_ITEM_ID] as? Int

    fun getTargetSlotId(player: Player): Int? = player.temporaryAttribute()[ATTR_SLOT_ID] as? Int

    private fun clearSpellTargets(player: Player) {
        val attrs = player.temporaryAttribute()
        attrs.remove(ATTR_TARGET)
        attrs.remove(ATTR_ITEM_ID)
        attrs.remove(ATTR_SLOT_ID)
        attrs.remove(ATTR_OBJECT)
        attrs.remove(ATTR_FLOOR_ITEM)
    }

    fun sendTeleportSpell(
        player: Player,
        tile: WorldTile,
    ) {
        sendTeleportSpell(
            player,
            if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 9599 else 8939,
            if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 1681 else 1576,
            null,
            tile,
        )
    }

    fun sendTeleportSpell(
        player: Player,
        upEmoteId: Int,
        upGraphicId: Int,
        spell: Spell?,
        tile: WorldTile,
    ) {
        if (player.controlerManager.controler.let {
                it is FfaZone || it is CrucibleController || it is FightKiln || it is FightCaves
            }
        ) {
            player.packets.sendGameMessage("You cannot teleport out of here.")
            return
        }

        if (player.isTeleportBlocked) {
            player.message("You are teleport blocked!")
            return
        }

        if (!player.controlerManager.processMagicTeleport(tile)) {
            return
        }

        if (spell != null) {
            if (!RuneService.hasRunes(player, spell.runes)) return
            RuneService.consumeRunes(player, spell.runes)
        }

        val delay = if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 6 else 4
        player.tickManager.addTicks(TickManager.TickKeys.TELEPORTING_TICK, delay + 2)
        player.lock(delay)
        player.stopAll()
        player.resetReceivedHits()

        if (spell != null) {
            player.skills.addXp(Skills.MAGIC, spell.xp)
        }

        if (upEmoteId != -1) {
            player.animate(Animation(upEmoteId))
        }

        if (upGraphicId != -1) {
            player.gfx(Graphics(upGraphicId))
        }

        player.packets.sendSound(5527, 0, 2)

        WorldTasksManager.schedule(
            object : WorldTask() {
                override fun run() {
                    if (tile.x == 3222 && tile.y == 3222) {
                        player.freeze(0)
                        player.interfaceManager.closeChatBoxInterface()
                        player.controlerManager.forceStop()
                        player.house.enterMyHouse()
                        stop()
                        return
                    }

                    var teleTile = tile
                    if (spell != null &&
                        spell.name.contains("home", ignoreCase = true) &&
                        (EdgevillePvPController.isAtPvP(player) || EdgevillePvPController.isAtBank(player))
                    ) {
                        teleTile = WorldTile(85, 80, 0)
                    }

                    val baseTile = tile
                    repeat(10) {
                        val dx = Utils.random(-1, 1)
                        val dy = Utils.random(-1, 1)
                        val testTile = WorldTile(baseTile.x + dx, baseTile.y + dy, baseTile.plane)

                        if (World.canMoveNPC(testTile.plane, testTile.x, testTile.y, player.size)) {
                            teleTile = testTile
                            return@repeat
                        }
                    }

                    if (player.controlerManager.controler != null) {
                        player.controlerManager.controler.magicTeleported(0)
                    }

                    player.nextWorldTile = teleTile
                    player.animate(Animation(if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) -1 else 8941))
                    player.gfx(Graphics(if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) -1 else 1577))
                    player.packets.sendSound(5524, 0, 2)
                    player.setNextFaceWorldTile(WorldTile(teleTile.x, teleTile.y - 1, teleTile.plane))
                    player.direction = 6
                    player.unfreeze()
                    player.interfaceManager.closeChatBoxInterface()
                    stop()
                }
            },
            delay,
            0,
        )
    }
}
