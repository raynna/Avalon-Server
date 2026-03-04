@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.rs.kotlin.game.player.combat.magic

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.*
import com.rs.java.game.item.FloorItem
import com.rs.java.game.item.ground.GroundItems
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData
import com.rs.java.game.minigames.clanwars.FfaZone
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.actions.combat.modernspells.Alchemy
import com.rs.java.game.player.actions.combat.modernspells.BonesTo
import com.rs.java.game.player.actions.combat.modernspells.ChargeOrb
import com.rs.java.game.player.actions.combat.modernspells.SuperHeat
import com.rs.java.game.player.actions.skills.crafting.Enchanting
import com.rs.java.game.player.controllers.CrucibleController
import com.rs.java.game.player.controllers.EdgevillePvPController
import com.rs.java.game.player.controllers.FightCaves
import com.rs.java.game.player.controllers.FightKiln
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatAction
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.game.world.projectile.ProjectileManager

object SpellHandler {
    @JvmStatic
    fun selectCombatSpell(
        player: Player,
        spellId: Int,
    ) {
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
            SpellType.Combat -> {
                handleCombatSpell(player, spell, autocast)
            }

            SpellType.Teleport -> {
                handleTeleportSpell(player, spell)
            }

            SpellType.Instant -> {
                castInstant(player, spell)
            }

            SpellType.Item -> {
                handleItemSpell(player, spell)
            }

            SpellType.FloorItem -> {
                handleTelegrabSpell(player, spell)
            }

            is SpellType.Object -> {
                handleOrbChargingSpell(player, spell)
            }

            SpellType.Target -> {
                handleTeleportOtherSpell(player, spell)
            }
        }
    }

    fun castOnItem(
        player: Player,
        spellId: Int,
        itemId: Int,
        slotId: Int,
    ) {
        player.temporaryAttribute().apply {
            put("spell_itemid", itemId)
            put("spell_slotid", slotId)
        }
        cast(player, spellId, false)
    }

    private fun handleItemSpell(
        player: Player,
        spell: Spell,
    ) {
        val attrs = player.temporaryAttribute()
        val itemId = attrs["spell_itemid"] as? Int ?: return
        val slotId = attrs["spell_slotid"] as? Int ?: return

        when {
            spell.name.contains("alchemy", true) -> {
                handleAlchemySpell(player, spell, itemId, slotId)
            }

            spell.name.contains("enchant", true) -> {
                handleEnchantSpell(player, spell, itemId, slotId)
            }

            spell.name.contains("superheat", ignoreCase = true) -> {
                if (!RuneService.hasRunes(player, spell.runes)) return
                if (SuperHeat.cast(player, spell.xp, itemId, slotId)) {
                    RuneService.consumeRunes(player, spell.runes)
                    return
                }
            }

            else -> {
                player.message("Unhandled ${spell.name}")
            }
        }
    }

    private fun castInstant(
        player: Player,
        spell: Spell,
    ) {
        player.message("cast Instant: " + spell.name)
        when (spell.name) {
            "Bones to Bananas", "Bones to Peaches" -> {
                if (BonesTo.cast(player, spell.xp, spell.name.contains("Peaches"))) {
                    checkAndRemoveRunes(player, spell)
                }
            }

            "Charge" -> {
                player.animate(811)
                player.tickManager.addMinutes(TickManager.TickKeys.CHARGE_SPELL, 2)
                player.message("You are now feeling the power of the charge spell.")
            }

            "Enchant Crossbow bolt" -> {
                player.stopAll()
                player.interfaceManager.sendInterface(432)
            }
        }
    }

    fun castOnObject(
        player: Player,
        spellId: Int,
        objectId: Int,
    ) {
        player.temporaryAttribute()["spell_objectid"] = objectId
        cast(player, spellId, false)
    }

    fun castOnFloorItem(
        player: Player,
        spellId: Int,
        floorItem: FloorItem,
    ) {
        player.temporaryAttribute()["spell_flooritem"] = floorItem
        cast(player, spellId, false)
    }

    @JvmStatic
    fun castOnPlayer(
        player: Player,
        spellId: Int,
        target: Player,
    ) {
        player.temporaryAttribute()["spell_target"] = target
        cast(player, spellId, false)
    }

    @JvmStatic
    fun castOnNpc(
        player: Player,
        spellId: Int,
        target: NPC,
    ) {
        player.temporaryAttribute()["spell_target"] = target
        cast(player, spellId, false)
    }

    fun getSpellForPlayer(
        player: Player,
        spellId: Int,
    ): Spell? =
        when (player.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }

    private fun canCast(
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
                player.packets.sendGameMessage(
                    "You don't have the required staff equipped to cast this spell.",
                )
                return false
            }
        }

        spell.itemRequirement?.let { req ->

            if (req.anyOf.isNotEmpty() &&
                req.anyOf.none { player.equipment.containsOneItem(it) }
            ) {
                player.packets.sendGameMessage(
                    "You don't have the required item to cast this spell.",
                )
                return false
            }

            if (req.allOf.isNotEmpty() &&
                !req.allOf.all { player.equipment.containsOneItem(it) }
            ) {
                player.packets.sendGameMessage(
                    "You don't have all required items to cast this spell.",
                )
                return false
            }
        }

        return true
    }

    private fun staffOfLightEffect(player: Player): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt()) ?: return false
        return when {
            weapon.isAnyOf("item.kodai_wand") -> {
                Utils.roll(3, 20)
            }

            weapon.isAnyOf(
                "item.staff_of_light",
                "item.staff_of_light_red",
                "item.staff_of_light_blue",
                "item.staff_of_light_green",
                "item.staff_of_light_gold",
            ) -> {
                Utils.roll(1, 8)
            }

            else -> {
                false
            }
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
                "Your spell draws its power completely from your " +
                    if (isKodai) "wand." else "staff.",
            )
            return true
        }

        RuneService.consumeRunes(player, spell.runes)

        return true
    }

    private fun handleCombatSpell(
        player: Player,
        spell: Spell,
        autocast: Boolean,
    ) {
        if (!autocast) {
            player.temporaryAttribute()["tempCastSpell"] = spell.id
        }
        val target =
            player.temporaryAttribute()["spell_target"] as? Entity ?: run {
                player.message("Invalid spell target")
                return
            }
        player.actionManager.setAction(CombatAction(target))
    }

    private fun handleTeleportSpell(
        player: Player,
        spell: Spell,
    ) {
        spell.teleportLocation?.let { location ->
            sendTeleportSpell(
                player,
                if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 9599 else 8939,
                if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 1681 else 1576,
                spell,
                location,
            )
        }
    }

    private fun handleAlchemySpell(
        player: Player,
        spell: Spell,
        itemId: Int,
        slotId: Int,
    ) {
        if (!RuneService.hasRunes(player, spell.runes)) {
            return
        }
        val fireStaff = player.equipment.weaponId in listOf(1387, 1393, 1401, 3053, 3054)
        val isHighAlchemy = spell.name.contains("High", true)
        if (Alchemy.castSpell(player, itemId, slotId, fireStaff, !isHighAlchemy)) {
            RuneService.consumeRunes(player, spell.runes)
        }
    }

    private fun handleEnchantSpell(
        player: Player,
        spell: Spell,
        itemId: Int,
        slotId: Int,
    ) {
        val enchantLevel =
            when (spell.id) {
                29 -> 1
                41 -> 2
                53 -> 3
                61 -> 4
                76 -> 5
                88 -> 6
                else -> return
            }

        Enchanting.startEnchant(player, itemId, slotId, enchantLevel, spell.xp)
    }

    private fun handleOrbChargingSpell(
        player: Player,
        spell: Spell,
    ) {
        val objectId = player.temporaryAttribute()["spell_objectid"] as? Int ?: return

        val type = spell.type as? SpellType.Object ?: return

        if (objectId !in type.objectIds) {
            player.message("Nothing interesting happens.")
            return
        }

        if (!player.inventory.containsItem("item.unpowered_orb")) {
            player.message("You need an unpowered orb to charge.")
            return
        }

        player.dialogueManager.startDialogue(
            "ChargeOrbD",
            spell,
            type.orbItemId,
        )
    }

    private fun handleTelegrabSpell(
        player: Player,
        spell: Spell,
    ) {
        if (!RuneService.hasRunes(player, spell.runes)) {
            return
        }
        RuneService.consumeRunes(player, spell.runes)
        val attrs = player.temporaryAttribute()
        val floorItem = attrs["spell_flooritem"] as? FloorItem ?: return

        val tile = floorItem.tile
        val itemId = floorItem.id

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

            if (!player.inventory.hasFreeSlots()) {
                player.message("You don't have enough inventory space.")
                return@sendToTile
            }

            World.sendGraphics(player, Graphics(144), tile)

            GroundItems.removeGroundItem(player, currentItem)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleTeleportOtherSpell(
        player: Player,
        spell: Spell,
    ) {
        val target = player.temporaryAttribute()["spell_target"] as? Player ?: return
        player.message("Teleport ${target.displayName} to ${spell.name}")
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

    @Suppress("UNUSED_VARIABLES")
    fun sendTeleportSpell(
        player: Player,
        upEmoteId: Int,
        upGraphicId: Int,
        spell: Spell?,
        tile: WorldTile,
    ) {
        if (player.controlerManager.controler.let {
                it is FfaZone || it is CrucibleController ||
                    it is FightKiln || it is FightCaves
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
                    if (tile.x == 3222 && tile.y == 3222) { // House teleport
                        player.freeze(0)
                        player.interfaceManager.closeChatBoxInterface()
                        player.controlerManager.forceStop()
                        player.house.enterMyHouse()
                        stop()
                        return
                    }

                    var teleTile = tile
                    if (spell != null) {
                        if (spell.name.contains(
                                "home",
                                ignoreCase = true,
                            ) && (EdgevillePvPController.isAtPvP(player) || EdgevillePvPController.isAtBank(player))
                        ) {
                            teleTile = WorldTile(85, 80, 0)
                        }
                    }
                    val baseTile = tile

                    repeat(10) {
                        val dx = Utils.random(-1, 1)
                        val dy = Utils.random(-1, 1)
                        val testTile =
                            WorldTile(
                                baseTile.x + dx,
                                baseTile.y + dy,
                                baseTile.plane,
                            )

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
