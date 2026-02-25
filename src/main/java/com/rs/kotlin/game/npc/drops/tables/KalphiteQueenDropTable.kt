package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object KalphiteQueenDropTable {
    val table =
        dropTable(
            rareTable = RareTableConfig(numerator = 1, denominator = 126),
            category = TableCategory.BOSS,
        ) {

            charm {
                gold(amount = 1, percent = 9.37)
                green(amount = 1, percent = 20.7)
                crimson(amount = 1, percent = 30.3)
                blue(amount = 1, percent = 23.3)
            }

            main(126) {
                // Weapons & armour
                drop("item.battlestaff_noted", amount = 10, weight = 5)
                drop("item.rune_chainbody", weight = 4)
                drop("item.red_d_hide_body", weight = 4)
                drop("item.rune_knife_p_3", amount = 25, weight = 4)
                drop("item.lava_battlestaff", weight = 2)
                // Runes
                drop("item.death_rune", amount = 150, weight = 6)
                drop("item.blood_rune", amount = 100, weight = 6)
                drop("item.mithril_arrow", amount = 500, weight = 5)
                drop("item.rune_arrow", amount = 250, weight = 3)
                // Herbs
                drop("item.grimy_toadflax_noted", amount = 25, weight = 2)
                drop("item.grimy_ranarr_noted", amount = 25, weight = 2)
                drop("item.grimy_snapdragon_noted", amount = 25, weight = 2)
                drop("item.grimy_torstol_noted", amount = 25, weight = 2)
                // Seeds
                drop("item.torstol_seed", amount = 2, weight = 4)
                drop("item.watermelon_seed", amount = 25, weight = 3)
                drop("item.papaya_tree_seed", amount = 2, weight = 3)
                drop("item.palm_tree_seed", amount = 2, weight = 3)
                drop("item.magic_seed", amount = 2, weight = 3)
                // Resources
                drop("item.rune_bar_noted", amount = 3, weight = 5)
                drop("item.bucket_of_sand_noted", amount = 100, weight = 4)
                drop("item.gold_ore_noted", amount = 250, weight = 4)
                drop("item.magic_logs_noted", amount = 60, weight = 4)
                drop("item.uncut_emerald_noted", amount = 25, weight = 3)
                drop("item.uncut_ruby_noted", amount = 25, weight = 3)
                drop("item.uncut_diamond_noted", amount = 25, weight = 3)
                // Other
                drop("item.wine_of_zamorak_noted", amount = 60, weight = 10)
                drop("item.potato_cactus_noted", amount = 100, weight = 8)
                drop("item.coins", amount = 15000..20000, weight = 5)
                drop("item.grapes_noted", amount = 100, weight = 5)
                drop("item.weapon_poison_++_noted", amount = 5, weight = 5)
                drop("item.cactus_spine_noted", amount = 10, weight = 3)
            }

            tertiary {
                drop("item.dragon_chainbody", numerator = 1, denominator = 128) { collectionLog = true }
                drop("item.dragon_2h_sword", numerator = 1, denominator = 128) { collectionLog = true }
                drop("item.dragon_pickaxe", numerator = 1, denominator = 128) { collectionLog = true }
                drop("item.kq_head", numerator = 1, denominator = 128) { collectionLog = true }
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 100,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "Kalphite Queen" }
}
