package com.rs.kotlin.game.npc.drops.tables

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.item.Item
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.MonsterCategory
import com.rs.kotlin.game.npc.drops.dropTable

object RevenantDropTable {

    val table = dropTable(
        category = MonsterCategory.SLAYER,
        rareDropTable = true,
        rolls = 1) {

        preRollDrops {
            drop("item.craw_s_bow", numerator = 1, denominator = 6000)
            drop("item.thammaron_s_sceptre", numerator = 1, denominator = 6000)
            drop("item.viggora_s_chainmace", numerator = 1, denominator = 6000)
            listOf(
                "item.statius_s_full_helm", "item.statius_s_platebody", "item.statius_s_platelegs",
                "item.vesta_s_chainbody", "item.vesta_s_plateskirt", "item.vesta_s_longsword", "item.vesta_s_spear",
                "item.zuriel_s_hood", "item.zuriel_s_robe_top", "item.zuriel_s_robe_bottom", "item.zuriel_s_staff",
                "item.morrigan_s_coif", "item.morrigan_s_leather_body", "item.morrigan_s_leather_chaps", "item.morrigan_s_javelin", "item.morrigan_s_throwing_axe"
            ).forEach { key ->

                val id = Item.getId(key)
                val defs = ItemDefinitions.getItemDefinitions(id)

                val amount = if (defs.isStackable) {
                    25..50
                } else {
                    1..1
                }

                drop(key, amount = amount, numerator = 1, denominator = 3000)
            }

            listOf(
                "item.corrupt_statius_s_platebody",
                "item.corrupt_statius_s_platelegs",
                "item.corrupt_statius_s_full_helm",
                "item.corrupt_vesta_s_chainbody",
                "item.corrupt_vesta_s_plateskirt",
                "item.corrupt_vesta_s_longsword",
                "item.corrupt_vesta_s_spear",
                "item.corrupt_zuriel_s_hood",
                "item.corrupt_zuriel_s_robe_top",
                "item.corrupt_zuriel_s_robe_bottom",
                "item.corrupt_zuriel_s_staff",
                "item.corrupt_morrigan_s_coif",
                "item.corrupt_morrigan_s_leather_body",
                "item.corrupt_morrigan_s_leather_chaps"
            ).forEach { drop(it, numerator = 1, denominator = 1500) }
            listOf(
                "item.broken_statue_headdress",
                "item.bronzed_dragon_claw",
                "item.ruby_chalice",
                "item.seren_statuette",
                "item.zamorak_statuette",
                "item.saradomin_statuette",
                "item.bandos_statuette",
                "item.armadyl_statuette",
                "item.guthixian_brazier",
                "item.bandos_scrimshaw",
                "item.ancient_psaltery_bridge",
                "item.third_age_carafe",
                "item.saradomin_amphora",
                "item.zamorak_medallion",
                "item.saradomin_carving",
                "item.armadyl_totem",
            ).forEach { drop(it, numerator = 1, denominator = 500) }

        }

        mainDrops(512) {
            drop("item.battlestaff_noted", amount = 4, weight = 10)
            drop("item.rune_full_helm", amount = 2, weight = 10)
            drop("item.rune_platebody", amount = 2, weight = 10)
            drop("item.rune_platelegs", amount = 2, weight = 10)
            drop("item.rune_kiteshield", amount = 2, weight = 10)
            drop("item.rune_warhammer", amount = 2, weight = 10)
            drop("item.dragon_dagger", amount = 2, weight = 5)
            drop("item.dragon_longsword", amount = 2, weight = 5)
            drop("item.dragon_platelegs", amount = 1..2, weight = 2)
            drop("item.dragon_plateskirt", amount = 1..2, weight = 2)
            drop("item.dragon_helm", amount = 1, weight = 1)

            drop("item.coal_noted", amount = 30..60, weight = 32)
            drop("item.adamant_bar_noted", amount = 4..6, weight = 32)
            drop("item.manta_ray", amount = 10..15, weight = 20)
            drop("item.runite_ore_noted", amount = 2..3, weight = 20)
            drop("item.black_dragonhide_noted", amount = 4, weight = 20)
            drop("item.yew_logs_noted", amount = 20..40, weight = 20)
            drop("item.super_restore_4", amount = 1..3, weight = 15)
            drop("item.rune_bar_noted", amount = 2..3, weight = 15)
            drop("item.mahogany_plank_noted", amount = 8..16, weight = 15)
            drop("item.magic_logs_noted", amount = 8..16, weight = 10)
            drop("item.uncut_dragonstone_noted", amount = 2..5, weight = 5)
            drop("item.yew_seed", amount = 2..6, weight = 1)
            drop("item.magic_seed", amount = 2..6, weight = 1)

            drop("item.law_rune", amount = 20..45, weight = 25)
            drop("item.death_rune", amount = 30..60, weight = 25)
            drop("item.blood_rune", amount = 50..100, weight = 25)
            drop("item.dragonstone_bolt_tips", amount = 20..40, weight = 15)
            drop("item.onyx_bolt_tips", amount = 3..6, weight = 15)
        }

        tertiaryDrops {
            drop(
                "item.forinthry_brace_5",
                numerator = 1,
                denominator = 100)
        }
    }.apply { name = "Revenant" }
}
