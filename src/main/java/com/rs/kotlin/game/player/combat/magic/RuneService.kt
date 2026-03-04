package com.rs.kotlin.game.player.combat.magic

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.item.meta.RunePouchMetaData
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player

object RuneService {
    fun hasRunes(
        player: Player,
        requirements: List<RuneRequirement>,
    ): Boolean {
        val pool = buildRunePool(player)

        expandCombinationRunes(pool)

        for (req in requirements) {
            if (req.canBeInfinite && hasInfiniteRune(player, req.id)) {
                continue
            }

            val available = pool[req.id] ?: 0

            if (available < req.amount) {
                player.packets.sendGameMessage(
                    "You don't have enough ${ItemDefinitions.getItemDefinitions(req.id).name}.",
                )
                return false
            }
        }

        return true
    }

    fun consumeRunes(
        player: Player,
        requirements: List<RuneRequirement>,
    ) {
        for (req in requirements) {
            if (req.canBeInfinite && hasInfiniteRune(player, req.id)) {
                continue
            }

            var remaining = req.amount

            val invAmount = player.inventory.getNumberOf(req.id)

            val remove = minOf(invAmount, remaining)

            if (remove > 0) {
                player.inventory.deleteItem(req.id, remove)
                remaining -= remove
            }

            if (remaining <= 0) continue

            val combos = RuneDefinitions.getCombinationRunesFor(req.id)

            for (combo in combos) {
                val comboAmount = player.inventory.getNumberOf(combo)

                val removeCombo = minOf(comboAmount, remaining)

                if (removeCombo > 0) {
                    player.inventory.deleteItem(combo, removeCombo)
                    remaining -= removeCombo
                }

                if (remaining <= 0) break
            }

            if (remaining > 0) {
                removeRuneFromPouch(player, req.id, remaining)
            }
        }

        player.inventory.refresh()
    }

    private fun buildRunePool(player: Player): MutableMap<Int, Int> {
        val pool = mutableMapOf<Int, Int>()

        val items = player.inventory.items.containerItems

        for (item in items) {
            if (item == null) continue

            val id = item.id
            val amount = item.amount

            pool[id] = (pool[id] ?: 0) + amount
        }

        for (item in items) {
            if (item?.id == RuneDefinitions.RUNE_POUCH) {
                val meta = item.metadata

                if (meta is RunePouchMetaData) {
                    for ((runeId, amount) in meta.runes) {
                        pool[runeId] = (pool[runeId] ?: 0) + amount
                    }
                }
            }
        }

        return pool
    }

    private fun expandCombinationRunes(pool: MutableMap<Int, Int>) {
        for ((comboRune, elements) in RuneDefinitions.combinationRunes) {
            val amount = pool[comboRune] ?: continue

            for (element in elements) {
                pool[element] = (pool[element] ?: 0) + amount
            }
        }
    }

    private fun removeRuneFromPouch(
        player: Player,
        runeId: Int,
        amount: Int,
    ) {
        var remaining = amount

        val items = player.inventory.items.containerItems

        for (item in items) {
            if (item?.id != RuneDefinitions.RUNE_POUCH) continue

            val meta = item.metadata

            if (meta is RunePouchMetaData) {
                val runes = meta.runes.toMutableMap()

                val available = runes[runeId] ?: 0
                val remove = minOf(available, remaining)

                if (remove > 0) {
                    runes[runeId] = available - remove
                    remaining -= remove
                }

                if (remaining <= 0) {
                    meta.updateRunes(runes)
                    return
                }

                // try combo runes inside pouch
                val combos = RuneDefinitions.getCombinationRunesFor(runeId)

                for (combo in combos) {
                    val comboAmount = runes[combo] ?: 0
                    val removeCombo = minOf(comboAmount, remaining)

                    if (removeCombo > 0) {
                        runes[combo] = comboAmount - removeCombo
                        remaining -= removeCombo
                    }

                    if (remaining <= 0) {
                        meta.updateRunes(runes)
                        return
                    }
                }

                meta.updateRunes(runes)
            }
        }
    }

    private fun hasInfiniteRune(
        player: Player,
        runeId: Int,
    ): Boolean {
        val weapon = player.equipment.getItem(Equipment.SLOT_WEAPON.toInt())
        val shield = player.equipment.getItem(Equipment.SLOT_SHIELD.toInt())

        return when (runeId) {
            RuneDefinitions.Runes.FIRE -> {
                weapon?.isAnyOf(
                    "item.staff_of_fire",
                    "item.fire_battlestaff",
                    "item.mystic_fire_staff",
                    "item.lava_battlestaff",
                    "item.mystic_lava_staff",
                    "item.steam_battlestaff",
                    "item.mystic_steam_staff",
                ) == true
            }

            RuneDefinitions.Runes.WATER -> {
                weapon?.isAnyOf(
                    "item.staff_of_water",
                    "item.water_battlestaff",
                    "item.mystic_water_staff",
                    "item.mud_battlestaff",
                    "item.mystic_mud_staff",
                    "item.steam_battlestaff",
                    "item.mystic_steam_staff",
                ) == true || shield?.isAnyOf("item.tome_of_frost") == true
            }

            RuneDefinitions.Runes.EARTH -> {
                weapon?.isAnyOf(
                    "item.staff_of_earth",
                    "item.earth_battlestaff",
                    "item.mystic_earth_staff",
                    "item.lava_battlestaff",
                    "item.mystic_lava_staff",
                    "item.mud_battlestaff",
                    "item.mystic_mud_staff",
                ) == true
            }

            RuneDefinitions.Runes.AIR -> {
                weapon?.isAnyOf(
                    "item.staff_of_air",
                    "item.air_battlestaff",
                    "item.mystic_air_staff",
                    "item.armadyl_battlestaff",
                ) == true
            }

            else -> {
                false
            }
        }
    }
}
