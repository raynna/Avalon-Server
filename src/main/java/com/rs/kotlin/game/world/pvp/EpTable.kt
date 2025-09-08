package com.rs.kotlin.game.world.pvp

import com.rs.kotlin.game.npc.drops.dropTable

object EpTable {

    val main = dropTable(rolls = 3) {
        alwaysDrops {
            // Always give coins or some base loot
            drop("item.coins", amount = 150_000..400_000)
        }

        mainDrops(512) {
            // PvP statuettes (all of them)
            drop("item.seren_statuette", weight = 20)
            drop("item.zamorak_statuette", weight = 20)
            drop("item.saradomin_statuette", weight = 20)
            drop("item.bandos_statuette", weight = 20)
            drop("item.armadyl_statuette", weight = 15)
            drop("item.ruby_chalice", weight = 12)
            drop("item.guthixian_brazier", weight = 12)
            drop("item.armadyl_totem", weight = 10)
            drop("item.zamorak_medallion", weight = 10)
            drop("item.saradomin_carving", weight = 10)
            drop("item.bandos_scrimshaw", weight = 10)
            drop("item.saradomin_amphora", weight = 8)
            drop("item.ancient_psaltery_bridge", weight = 8)
            drop("item.bronzed_dragon_claw", weight = 6)
            drop("item.third_age_carafe", weight = 6)
            drop("item.ancient_statuette", weight = 5)

            // Statius's equipment
            drop("item.statius_s_platebody", weight = 2)
            drop("item.statius_s_platelegs", weight = 2)
            drop("item.statius_s_full_helm", weight = 2)
            drop("item.statius_s_warhammer", weight = 2)

            // Vesta's equipment
            drop("item.vesta_s_chainbody", weight = 2)
            drop("item.vesta_s_plateskirt", weight = 2)
            drop("item.vesta_s_longsword", weight = 2)
            drop("item.vesta_s_spear", weight = 2)

            // Zuriel's equipment
            drop("item.zuriel_s_robe_top", weight = 2)
            drop("item.zuriel_s_robe_bottom", weight = 2)
            drop("item.zuriel_s_staff", weight = 2)

            // Morrigan's equipment
            drop("item.morrigan_s_leather_body", weight = 2)
            drop("item.morrigan_s_leather_chaps", weight = 2)
            drop("item.morrigan_s_javelin", amount = 25..50, weight = 3)
            drop("item.morrigan_s_throwing_axe", amount = 25..50, weight = 3)

            // Corrupt versions (slightly more common)
            listOf(
                "corrupt_statius_s_platebody",
                "corrupt_statius_s_platelegs",
                "corrupt_statius_s_full_helm",
                "corrupt_statius_s_warhammer",
                "corrupt_vesta_s_chainbody",
                "corrupt_vesta_s_plateskirt",
                "corrupt_vesta_s_longsword",
                "corrupt_vesta_s_spear",
                "corrupt_zuriel_s_robe_top",
                "corrupt_zuriel_s_robe_bottom",
                "corrupt_zuriel_s_staff",
                "corrupt_morrigan_s_leather_body",
                "corrupt_morrigan_s_leather_chaps",
                "corrupt_morrigan_s_javelin",
                "corrupt_morrigan_s_throwing_axe"
            ).forEach { drop("item.$it", weight = 4) }
        }
    }.apply { name = "EP PvP Table" }
}
