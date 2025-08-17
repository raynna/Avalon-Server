package com.rs.kotlin.game.player.combat.magic

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.*
import com.rs.java.game.item.Item
import com.rs.java.game.minigames.clanwars.FfaZone
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.java.game.player.controlers.*
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.item.FloorItem
import com.rs.java.game.item.meta.RunePouchMetaData
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.TickManager
import com.rs.java.game.player.actions.combat.modernspells.Alchemy
import com.rs.java.game.player.actions.combat.modernspells.BonesTo
import com.rs.java.game.player.actions.combat.modernspells.Charge
import com.rs.java.game.player.actions.combat.modernspells.ChargeOrb
import com.rs.java.game.player.actions.skills.crafting.Enchanting
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatAction

object SpellHandler {

    @JvmStatic
    fun selectCombatSpell(player: Player, spellId: Int) {
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

    fun cast(player: Player, spellId: Int, autocast: Boolean = false) {
        val spell = getSpellForPlayer(player, spellId) ?: return
        if (!canCast(player, spell)) return
        if (spell.type != SpellType.Combat) {
            if (!checkAndRemoveRunes(player, spell)) return
        }
        if (spell.type == SpellType.Object) {
            if (spell.name.equals("Charge Water Orb", ignoreCase = true)) {
                handleOrbChargingSpell(player, spell)
            }
        }
        when (spell.type) {
            is SpellType.Combat -> handleCombatSpell(player, spell, autocast)
            is SpellType.Teleport -> handleTeleportSpell(player, spell)
            SpellType.FloorItem -> TODO()
            SpellType.Instant -> castInstant(player, spell)
            SpellType.Item -> TODO()
            SpellType.Object -> TODO()
            SpellType.Target -> TODO()
        }
    }

    fun castOnItem(player: Player, spellId: Int, itemId: Int, slotId: Int) {
        player.temporaryAttribute().apply {
            put("spell_itemid", itemId)
            put("spell_slotid", slotId)
        }
        cast(player, spellId, false)
    }

    private fun castInstant(player: Player, spell: Spell) {
        if (spell.name.equals("charge", ignoreCase = true)) {
            player.animate(811)
        }
    }

    fun castOnObject(player: Player, spellId: Int, objectId: Int) {
        player.temporaryAttribute()["spell_objectid"] = objectId
        cast(player, spellId, false)
    }

    fun castOnFloorItem(player: Player, spellId: Int, floorItem: FloorItem) {
        player.temporaryAttribute()["spell_flooritem"] = floorItem
        cast(player, spellId, false)
    }

    @JvmStatic
    fun castOnPlayer(player: Player, spellId: Int, target: Player) {
        player.temporaryAttribute()["spell_target"] = target
        cast(player, spellId, false)
    }

    @JvmStatic
    fun castOnNpc(player: Player, spellId: Int, target: NPC) {
        player.temporaryAttribute()["spell_target"] = target
        cast(player, spellId, false)
    }

    fun getSpellForPlayer(player: Player, spellId: Int): Spell? {
        return when (player.combatDefinitions.getSpellBook()) {
            AncientMagicks.id -> AncientMagicks.getSpell(spellId)
            ModernMagicks.id -> ModernMagicks.getSpell(spellId)
            else -> null
        }
    }

    private fun canCast(player: Player, spell: Spell): Boolean {
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

        if (player.lockDelay > Utils.currentTimeMillis()) {
            return false
        }

        spell.staff?.let { staffReq ->
            if (staffReq.ids.isNotEmpty() && staffReq.ids.none { id ->
                    player.equipment.weaponId == id
                }) {
                player.packets.sendGameMessage("You don't have the required staff equipped to cast this spell.")
                return false
            }
        }

        spell.requiredItem?.let { itemId ->
            if (!player.inventory.containsOneItem(itemId)) {
                player.packets.sendGameMessage("You don't have the required item to cast this spell.")
                return false
            }
        }

        return true
    }

    fun checkAndRemoveRunes(player: Player, spell: Spell): Boolean {
        if (staffOfLightEffect(player)) {
            player.packets.sendGameMessage("Your spell draws its power completely from your staff.")
            return true
        }

        val runesToRemove = mutableListOf<Item>()

        for (requirement in spell.runes) {
            if (requirement.canBeInfinite && hasInfiniteRune(player, requirement.id)) {
                continue
            }

            val runeId = requirement.compositeRunes.firstOrNull { compositeId ->
                hasRune(player, compositeId, requirement.amount)
            } ?: requirement.id

            if (!hasRune(player, runeId, requirement.amount)) {
                player.packets.sendGameMessage(
                    "You don't have enough ${ItemDefinitions.getItemDefinitions(runeId).name} to cast this spell."
                )
                player.combatDefinitions.resetSpells(true)
                return false
            }

            runesToRemove.add(Item(runeId, requirement.amount))
        }

        runesToRemove.forEach { rune ->
            var removed = false

            for (item in player.inventory.items.toArray()) {
                if (item == null || item.id != Item.getId("item.rune_pouch")) continue

                val meta = item.metadata
                if (meta is RunePouchMetaData) {
                    val pouchRunes = meta.getRunesToArray()
                    for (pouchRune in pouchRunes) {
                        if (pouchRune.id == rune.id && pouchRune.amount >= rune.amount) {
                            pouchRune.amount -= rune.amount
                            meta.updateRunes(pouchRunes)
                            player.inventory.refresh()
                            removed = true
                            break
                        }
                    }
                }

                if (removed) break
            }

            // If not removed from pouch, remove from inventory
            if (!removed) {
                player.inventory.deleteItem(rune.id, rune.amount)
                player.inventory.refresh()
            }
        }

        return true
    }


    private fun hasInfiniteRune(player: Player, runeId: Int): Boolean {
        val weaponName = ItemDefinitions.getItemDefinitions(player.equipment.weaponId).name.lowercase()
        val shieldName = ItemDefinitions.getItemDefinitions(player.equipment.shieldId).name.lowercase()

        return when (runeId) {
            RuneDefinitions.Runes.FIRE -> weaponName.contains("fire") ||
                    weaponName.contains("lava") ||
                    weaponName.contains("steam") ||
                    weaponName.contains("smoke") ||
                    shieldName.contains("tome of fire")
            RuneDefinitions.Runes.WATER -> weaponName.contains("water") ||
                    weaponName.contains("mud") ||
                    weaponName.contains("steam") ||
                    weaponName.contains("mist") ||
                    shieldName.contains("tome of frost")
            RuneDefinitions.Runes.EARTH -> weaponName.contains("earth") ||
                    weaponName.contains("lava") ||
                    weaponName.contains("mud") ||
                    weaponName.contains("dust")
            RuneDefinitions.Runes.AIR -> weaponName.contains("air") ||
                    weaponName.contains("mist") ||
                    weaponName.contains("smoke") ||
                    weaponName.contains("dust") ||
                    player.equipment.weaponId == 21777
            else -> false
        }
    }

    private fun hasRune(player: Player, runeId: Int, amount: Int): Boolean {
        if (hasRunePouch(player)) {
            for (item in player.inventory.items.toArray()) {
                if (item == null || item.id != Item.getId("item.rune_pouch")) continue

                val meta = item.metadata
                if (meta is RunePouchMetaData) {
                    val runes = meta.runes
                    val countInPouch = runes[runeId] ?: 0
                    if (countInPouch >= amount) {
                        return true
                    }
                }
            }
        }

        // Check inventory as fallback
        return player.inventory.getNumberOf(runeId) >= amount
    }


    private fun hasRunePouch(player: Player): Boolean {
        return player.inventory.containsOneItem(RuneDefinitions.RUNE_POUCH)
    }

    private fun staffOfLightEffect(player: Player): Boolean {
        val weaponId = player.equipment.weaponId
        return (weaponId == 15486 || weaponId in listOf(22207, 22209, 22211, 22213)) &&
                Utils.getRandom(5) == 0
    }

    private fun handleCombatSpell(player: Player, spell: Spell, autocast: Boolean) {
        if (!autocast) {
            player.temporaryAttribute()["tempCastSpell"] = spell.id
        }
        val target = player.temporaryAttribute()["spell_target"] as? Entity ?: run {
            player.message("Invalid spell target")
            return
        }
        player.newActionManager.setAction(CombatAction(target))
    }

    private fun handleTeleportSpell(player: Player, spell: Spell) {
        spell.teleportLocation?.let { location ->
            sendTeleportSpell(
                player,
                if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 9599 else 8939,
                if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 1681 else 1576,
                spell.xp,
                location
            )
        }
    }

    private fun handleAlchemySpell(player: Player, spell: Spell) {
        val itemId = player.temporaryAttribute()["spell_itemid"] as? Int ?: return
        val slotId = player.temporaryAttribute()["spell_slotid"] as? Int ?: return

        val fireStaff = player.equipment.weaponId in listOf(1387, 1393, 1401, 3053, 3054)
        val isHighAlchemy = spell.name.contains("High", ignoreCase = true)

        if (Alchemy.castSpell(player, itemId, slotId, fireStaff, !isHighAlchemy)) {
            checkAndRemoveRunes(player, spell)
        }
    }

    private fun handleEnchantSpell(player: Player, spell: Spell) {
        val itemId = player.temporaryAttribute()["spell_itemid"] as? Int ?: return
        val slotId = player.temporaryAttribute()["spell_slotid"] as? Int ?: return

        val enchantLevel = when (spell.id) {
            29 -> 1
            41 -> 2
            53 -> 3
            61 -> 4
            76 -> 5
            88 -> 6
            else -> 0
        }

        if (Enchanting.startEnchant(player, itemId, slotId, enchantLevel, spell.xp)) {
            checkAndRemoveRunes(player, spell)
        }
    }

    private fun handleTransformationSpell(player: Player, spell: Spell) {
        when (spell.name) {
            "Bones to Bananas", "Bones to Peaches" -> {
                if (BonesTo.cast(player, spell.xp, spell.name.contains("Peaches"))) {
                    checkAndRemoveRunes(player, spell)
                }
            }
            "Charge" -> {
                if (Charge.castSpell(player)) {
                    checkAndRemoveRunes(player, spell)
                }
            }
        }
    }

    private fun handleOrbChargingSpell(player: Player, spell: Spell) {
        val objectId = player.temporaryAttribute()["spell_objectid"] as? Int ?: return

        when (spell.name) {
            "Air Orb" -> if (objectId == 2152) ChargeOrb.charge(player, 573)
            "Water Orb" -> if (objectId == 2151) ChargeOrb.charge(player, 571)
            "Earth Orb" -> if (objectId == 2154) ChargeOrb.charge(player, 575)
            "Fire Orb" -> if (objectId == 2153) ChargeOrb.charge(player, 569)
        }
    }

    @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
    private fun handleTelegrabSpell(player: Player, spell: Spell) {
        val _floorItem = player.temporaryAttribute()["spell_flooritem"] as? FloorItem ?: return
        player.message("Telegrab handler works")
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleTeleportOtherSpell(player: Player, spell: Spell) {
        val target = player.temporaryAttribute()["spell_target"] as? Player ?: return
        player.message("Teleport ${target.displayName} to ${spell.name}")
    }

    @Suppress("UNUSED_VARIABLES")
    fun sendTeleportSpell(
        player: Player,
        upEmoteId: Int,
        upGraphicId: Int,
        xp: Double,
        tile: WorldTile
    ) {
        if (player.controlerManager.controler.let { it is FfaZone || it is CrucibleControler ||
                    it is FightKiln || it is FightCaves }) {
            player.packets.sendGameMessage("You cannot teleport out of here.")
            return
        }

        if (!player.controlerManager.processMagicTeleport(tile)) {
            return
        }
        player.message("teleport new system");
        player.stopAll()
        player.resetReceivedHits()

        val delay = if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) 6 else 4
        player.tickManager.addTicks(TickManager.TickKeys.TELEPORTING_TICK, delay + 2)
        player.lock(delay)

        if (upEmoteId != -1) {
            player.animate(Animation(upEmoteId))
        }

        if (upGraphicId != -1) {
            player.gfx(Graphics(upGraphicId))
        }

        player.packets.sendSound(5527, 0, 2)

        WorldTasksManager.schedule(object : WorldTask() {
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
                for (trycount in 0 until 10) {
                    teleTile = WorldTile(tile, 2)
                    if (World.canMoveNPC(tile.plane, teleTile.x, teleTile.y, player.size)) {
                        break
                    }
                    teleTile = tile
                }

                player.nextWorldTile = teleTile
                player.animate(Animation(if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) -1 else 8941))
                player.gfx(Graphics(if (player.combatDefinitions.getSpellBook() == AncientMagicks.id) -1 else 1577))
                player.packets.sendSound(5524, 0, 2)
                player.setNextFaceWorldTile(WorldTile(teleTile.x, teleTile.y - 1, teleTile.plane))
                player.direction = 6
                player.unfreeze()
                player.interfaceManager.closeChatBoxInterface()
                player.controlerManager.forceStop()
                stop()
            }
        }, delay, 0)
    }
}