package com.rs.kotlin.game.player.skills.woodcutting

import com.rs.java.game.WorldObject
import com.rs.java.game.item.Item
import com.rs.java.game.item.ground.GroundItems
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.npc.drops.Drop
import com.rs.kotlin.game.npc.drops.DropTableRegistry.rollItemTable
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.world.util.Msg
import com.rs.kotlin.rscm.Rscm

object BirdNests {
    private val NEST_IDS by lazy {
        Rscm.lookupList("item_group.bird_s_nest_search").toSet()
    }

    private fun Item.isBirdNest(): Boolean = id in NEST_IDS

    @JvmStatic
    fun rollBirdNest(
        player: Player,
        tree: WorldObject,
    ) {
        var nestChance = 256

        val cape = player.equipment.getItem(Equipment.SLOT_CAPE.toInt())
        if (cape != null && cape.isAnyOf("item.woodcutting_cape", "item.woodcutting_cape_t")) {
            nestChance = (nestChance * 0.9).toInt()
        }

        val nestIds = Rscm.lookupList("item_group.bird_s_nest_search")
        if (nestIds.isEmpty()) return

        if (!Utils.roll(1, nestChance)) return

        val nestId = nestIds.random()

        GroundItems.dropItemNearRandom(
            Item(nestId),
            tree.tile,
            player,
            1,
        )

        Msg.collect(player, "A bird's nest falls out of the tree!")
    }

    @JvmStatic
    fun searchNest(
        player: Player,
        item: Item,
        slot: Int,
    ): Boolean {
        if (!item.isBirdNest()) {
            return false
        }
        if (!player.getInventory().hasFreeSlots()) {
            player.message("You don't have enough inventory space to search this.")
            return false
        }

        val drops: MutableList<Drop> = rollItemTable(player, item.id, 1.0) as MutableList<Drop>

        if (drops.isEmpty()) return false

        for (drop in drops) {
            if (!player.getInventory().canHold(drop.itemId, drop.amount)) {
                GroundItems.updateGroundItem(
                    Item(drop.itemId, drop.amount),
                    player.tile,
                    player,
                    60,
                )
                continue
            }

            player.getInventory().addItem(drop.itemId, drop.amount)
        }

        player.getInventory().deleteItem(slot, item)
        player.getInventory().addItem("item.bird_s_nest_empty", 1)

        player.message("You search the bird's nest.")

        return true
    }

    val seedTable =
        dropTable {
            main(992) {
                drop("item.acorn", weight = 214)
                drop("item.apple_tree_seed", weight = 170)
                drop("item.willow_seed", weight = 135)
                drop("item.banana_tree_seed", weight = 108)
                drop("item.orange_tree_seed", weight = 85)
                drop("item.curry_tree_seed", weight = 68)
                drop("item.maple_seed", weight = 54)
                drop("item.pineapple_seed", weight = 42)
                drop("item.papaya_tree_seed", weight = 34)
                drop("item.yew_seed", weight = 27)
                drop("item.palm_tree_seed", weight = 22)
                drop("item.calquat_tree_seed", weight = 17)
                drop("item.spirit_seed", weight = 11)
                drop("item.magic_seed", weight = 5)
            }
        }.apply {
            name = "Bird's Nest (Seed)"
            visibleInViewer = false
        }
    val wysen =
        dropTable {
            main(600) {
                drop("item.sweetcorn_seed", weight = 102)
                drop("item.strawberry_seed", weight = 100)
                drop("item.acorn", weight = 80)
                drop("item.limpwurt_seed", weight = 80)
                drop("item.watermelon_seed", weight = 70)
                drop("item.lantadyme_seed", weight = 30)
                drop("item.dwarf_weed_seed", weight = 30)
                drop("item.cadantine_seed", weight = 24)
                drop("item.willow_seed", weight = 16)
                drop("item.pineapple_seed", weight = 16)
                drop("item.calquat_tree_seed", weight = 12)
                drop("item.papaya_tree_seed", weight = 10)
                drop("item.maple_seed", weight = 6)
                drop("item.torstol_seed", weight = 4)
                drop("item.ranarr_seed", weight = 4)
                drop("item.snapdragon_seed", weight = 4)
                drop("item.yew_seed", weight = 4)
                drop("item.spirit_seed", weight = 4)
                drop("item.palm_tree_seed", weight = 4)
                drop("item.magic_seed", weight = 2)
            }
        }.apply {
            name = "Bird's Nest (Seed)"
            visibleInViewer = false
        }
    val ringTable =
        dropTable {
            main(100) {
                drop("item.sapphire_ring", weight = 40)
                drop("item.gold_ring", weight = 35)
                drop("item.emerald_ring", weight = 15)
                drop("item.ruby_ring", weight = 9)
                drop("item.diamond_ring", weight = 1)
            }
        }.apply {
            name = "Bird's Nest (Ring)"
            visibleInViewer = false
        }
    val redEgg =
        dropTable {
            main(1) {
                drop("item.red_bird_s_egg")
            }
        }.apply {
            name = "Bird's Nest (Red egg)"
            visibleInViewer = false
        }
    val blueEgg =
        dropTable {
            main(1) {
                drop("item.blue_bird_s_egg")
            }
        }.apply {
            name = "Bird's Nest (Blue egg)"
            visibleInViewer = false
        }
    val greenEgg =
        dropTable {
            main(1) {
                drop("item.green_bird_s_egg")
            }
        }.apply {
            name = "Bird's Nest (Green egg)"
            visibleInViewer = false
        }
    val ravenEgg =
        dropTable {
            main(1) {
                drop("item.raven_egg")
            }
        }.apply {
            name = "Bird's Nest (Raven egg)"
            visibleInViewer = false
        }

    val birdNestsViewer =
        dropTable(
            name = "Bird nests",
            sourceAction = "searching",
            herbTables = emptyList(),
        ) {
            main(256) {
                table(
                    seedTable.mainDrops,
                    weight = 1,
                    asSubTable = true,
                    name = "Seed nest",
                    icon = "item.bird_s_nest_seeds",
                )
                table(
                    wysen.mainDrops,
                    weight = 1,
                    asSubTable = true,
                    name = "Wysen nest",
                    icon = "item.bird_s_nest_wysen",
                )
                table(
                    ringTable.mainDrops,
                    weight = 1,
                    asSubTable = true,
                    name = "Ring nest",
                    icon = "item.bird_s_nest_ring",
                )
                table(
                    redEgg.mainDrops,
                    weight = 1,
                    asSubTable = true,
                    name = "Red egg nest",
                    icon = "item.bird_s_nest_red_egg",
                )
                table(
                    greenEgg.mainDrops,
                    weight = 1,
                    asSubTable = true,
                    name = "Green egg nest",
                    icon = "item.bird_s_nest_green_egg",
                )
                table(
                    blueEgg.mainDrops,
                    weight = 1,
                    asSubTable = true,
                    name = "Blue egg nest",
                    icon = "item.bird_s_nest_blue_egg",
                )
                table(
                    ravenEgg.mainDrops,
                    weight = 1,
                    asSubTable = true,
                    name = "Raven egg nest",
                    icon = "item.bird_s_nest_raven_egg",
                )
            }
        }.apply {
            ignoreParentChanceForDisplay = true
        }
}
