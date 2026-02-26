package com.rs.kotlin.game.npc.drops.tables.minigame

import com.rs.java.game.minigames.warriorguild.WarriorsGuild
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.drops.DropContext
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.HerbTableConfig
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object CyclopsDropTable {
    private val DEFENDER_TIERS =
        listOf(
            "item.bronze_defender",
            "item.iron_defender",
            "item.steel_defender",
            "item.black_defender",
            "item.mithril_defender",
            "item.adamant_defender",
            "item.rune_defender",
            "item.dragon_defender",
        )

    private fun nextDefender(context: DropContext): Int? {
        val player = context.player
        for (tier in DEFENDER_TIERS) {
            val itemId = Rscm.item(tier)
            if (player.controlerManager.controler !is WarriorsGuild) {
                return null
            }
            val shield = player.equipment.getItem(Equipment.SLOT_SHIELD.toInt())

            val hasDefender =
                player.inventory.containsOneItem(itemId) ||
                    (shield != null && shield.id == itemId)

            if (!hasDefender) {
                return itemId
            }
        }
        return null
    }

    val table =
        dropTable(
            herbTables = HerbTableConfig(numerator = 3, denominator = 100),
            seedTable = SeedTableConfig(SeedTableType.UNCOMMON, numerator = 1, denominator = 100),
            gemTable = GemTableConfig(numerator = 2, denominator = 100),
        ) {

            always {
                drop("item.big_bones")
            }

            prerollDenom {
                drop(
                    numerator = 1,
                    denominator = 50,
                    dynamicItem = { player -> nextDefender(player) },
                    displayItems = DEFENDER_TIERS,
                ) { collectionLog = true }
            }

            main(100) {
                drop("item.black_knife", 4..13, weight = 16)
                drop("item.steel_chainbody", weight = 2)
                drop("item.iron_2h_sword", weight = 2)
                drop("item.iron_chainbody", weight = 2)
                drop("item.steel_dagger", weight = 2)
                drop("item.steel_mace", weight = 2)
                drop("item.steel_sword", weight = 2)
                drop("item.steel_battleaxe", weight = 2)
                drop("item.steel_2h_sword", weight = 2)
                drop("item.steel_longsword", weight = 2)
                drop("item.steel_helm", weight = 2)
                drop("item.black_2h_sword", weight = 1)
                drop("item.mithril_dagger", weight = 1)
                drop("item.mithril_longsword", weight = 1)
                drop("item.adamant_mace", weight = 1)
                drop("item.black_sword", weight = 1)
                drop("item.black_longsword", weight = 1)
                drop("item.black_dagger", weight = 1)
                drop("item.adamant_2h_sword", weight = 1)

                drop("item.coins", amount = 3..102, weight = 31)
                drop("item.coins", amount = 5..204, weight = 10)
            }

            tertiary {
                drop(
                    "item.scroll_box_hard",
                    numerator = 1,
                    denominator = 512,
                    condition = { context ->
                        !context.player.treasureTrailsManager.hasClueScrollByLevel(
                            TreasureTrailsManager.HARD,
                        )
                    },
                )
                drop(
                    "item.long_bone",
                    numerator = 1,
                    denominator = 400,
                ) { collectionLog = true }
                drop(
                    "item.curved_bone",
                    numerator = 1,
                    denominator = 5012,
                ) { collectionLog = true }
            }
        }.apply { name = "Cyclops" }
}
