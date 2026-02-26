package com.rs.kotlin.game.npc.drops.tables.cluescroll

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable

object HardTreasureTrailTable {
    val table =
        dropTable(sourceAction = "opening", category = TableCategory.CLUE) {
            val potion =
                weightedTable {
                    packageDrop(weight = 1) {
                        dropMany(
                            "item.super_attack_4_noted" to 5,
                            "item.super_strength_4_noted" to 5,
                            "item.super_defence_4_noted" to 5,
                        )
                    }
                }
            val thirdAge =
                weightedTable {
                    add("item.third_age_full_helmet")
                    add("item.third_age_platebody")
                    add("item.third_age_platelegs")
                    add("item.third_age_kiteshield")
                    add("item.third_age_range_coif")
                    add("item.third_age_range_top")
                    add("item.third_age_range_legs")
                    add("item.third_age_vambraces")
                    add("item.third_age_mage_hat")
                    add("item.third_age_robe_top")
                    add("item.third_age_robe")
                    add("item.third_age_amulet")
                }
            val gilded =
                weightedTable {
                    add("item.gilded_full_helm")
                    add("item.gilded_platebody")
                    add("item.gilded_platelegs")
                    add("item.gilded_plateskirt")
                    add("item.gilded_kiteshield")
                }
            val mega =
                weightedTable(7) {
                    table(potion, weight = 1, asSubTable = true, name = "Potions", icon = "item.super_attack_4")
                    table(
                        thirdAge,
                        weight = 1,
                        asSubTable = true,
                        name = "Third age",
                        icon = "item.third_age_full_helmet",
                    )
                    table(gilded, weight = 5, asSubTable = true, name = "Gilded", icon = "item.gilded_platebody")
                }
            val heraldic =
                weightedTable {
                    add("item.rune_helm_h1")
                    add("item.rune_helm_h2")
                    add("item.rune_helm_h3")
                    add("item.rune_helm_h4")
                    add("item.rune_helm_h5")
                    add("item.rune_platebody_h1")
                    add("item.rune_platebody_h2")
                    add("item.rune_platebody_h3")
                    add("item.rune_platebody_h4")
                    add("item.rune_platebody_h5")
                    add("item.rune_platelegs_h1")
                    add("item.rune_platelegs_h2")
                    add("item.rune_platelegs_h3")
                    add("item.rune_platelegs_h4")
                    add("item.rune_platelegs_h5")
                    add("item.rune_plateskirt_h1")
                    add("item.rune_plateskirt_h2")
                    add("item.rune_plateskirt_h3")
                    add("item.rune_plateskirt_h4")
                    add("item.rune_plateskirt_h5")
                    add("item.rune_shield_h1")
                    add("item.rune_shield_h2")
                    add("item.rune_shield_h3")
                    add("item.rune_shield_h4")
                    add("item.rune_shield_h5")
                }
            val weaponsAndArmour =
                weightedTable {
                    add("item.rune_full_helm")
                    add("item.rune_platebody")
                    add("item.rune_platelegs")
                    add("item.rune_plateskirt")
                    add("item.rune_kiteshield")
                    add("item.rune_longsword")
                    add("item.rune_dagger")
                    add("item.rune_battleaxe")
                    add("item.rune_hatchet")
                    add("item.rune_pickaxe")
                    add("item.black_d_hide_body")
                    add("item.black_d_hide_chaps")
                    add("item.magic_shortbow")
                }
            val rare =
                weightedTable {
                    add("item.amulet_of_glory_t4")
                    add("item.robin_hood_hat")
                    add("item.enchanted_hat")
                    add("item.enchanted_top")
                    add("item.enchanted_robe")

                    add("item.rune_full_helm_t")
                    add("item.rune_platebody_t")
                    add("item.rune_platelegs_t")
                    add("item.rune_plateskirt_t")
                    add("item.rune_kiteshield_t")

                    add("item.rune_full_helm_g")
                    add("item.rune_platebody_g")
                    add("item.rune_platelegs_g")
                    add("item.rune_plateskirt_g")
                    add("item.rune_kiteshield_g")

                    add("item.zamorak_full_helm")
                    add("item.zamorak_platebody")
                    add("item.zamorak_platelegs")
                    add("item.zamorak_plateskirt")
                    add("item.zamorak_kiteshield")

                    add("item.guthix_full_helm")
                    add("item.guthix_platebody")
                    add("item.guthix_platelegs")
                    add("item.guthix_plateskirt")
                    add("item.guthix_kiteshield")

                    add("item.saradomin_full_helm")
                    add("item.saradomin_platebody")
                    add("item.saradomin_platelegs")
                    add("item.saradomin_plateskirt")
                    add("item.saradomin_kiteshield")

                    add("item.ancient_full_helm")
                    add("item.ancient_platebody")
                    add("item.ancient_platelegs")
                    add("item.ancient_plateskirt")
                    add("item.ancient_kiteshield")

                    add("item.armadyl_full_helm")
                    add("item.armadyl_platebody")
                    add("item.armadyl_platelegs")
                    add("item.armadyl_plateskirt")
                    add("item.armadyl_kiteshield")

                    add("item.bandos_full_helm")
                    add("item.bandos_platebody")
                    add("item.bandos_platelegs")
                    add("item.bandos_plateskirt")
                    add("item.bandos_kiteshield")

                    add("item.black_d_hide_body_g")
                    add("item.black_d_hide_chaps_g")
                    add("item.black_d_hide_body_t")
                    add("item.black_d_hide_chaps_t")

                    add("item.saradomin_coif")
                    add("item.saradomin_d_hide_body")
                    add("item.saradomin_d_hide_chaps")
                    add("item.saradomin_vambraces")

                    add("item.guthix_coif")
                    add("item.guthix_d_hide_body")
                    add("item.guthix_d_hide_chaps")
                    add("item.guthix_vambraces")

                    add("item.zamorak_coif")
                    add("item.zamorak_d_hide_body")
                    add("item.zamorak_d_hide_chaps")
                    add("item.zamorak_vambraces")

                    add("item.armadyl_coif")
                    add("item.armadyl_d_hide_body")
                    add("item.armadyl_d_hide_chaps")
                    add("item.armadyl_vambraces")

                    add("item.ancient_coif")
                    add("item.ancient_d_hide_body")
                    add("item.ancient_d_hide_chaps")
                    add("item.ancient_vambraces")

                    add("item.saradomin_stole")
                    add("item.saradomin_crozier")
                    add("item.guthix_stole")
                    add("item.guthix_crozier")
                    add("item.zamorak_stole")
                    add("item.zamorak_crozier")
                    add("item.tan_cavalier")
                    add("item.black_cavalier")
                    add("item.dark_cavalier")
                    table(heraldic, weight = 5, asSubTable = true, name = "Heralic", icon = "item.rune_platebody_h5")
                    table(mega, weight = 1)
                }
            val bow =
                weightedTable(10) {
                    add("item.magic_composite_bow", weight = 1)
                    add("item.magic_longbow", weight = 9)
                }
            val sharedScrolls =
                weightedTable {
                    // unknown
                    add("item.lumber_yard_teleport", amount = 5..15)
                    add("item.nardah_teleport", amount = 5..15)
                    add("item.bandit_camp_teleport", amount = 5..15)
                }
            val sharedPage =
                weightedTable {
                    add("item.zamorak_page_1")
                    add("item.zamorak_page_2")
                    add("item.zamorak_page_3")
                    add("item.zamorak_page_4")
                    add("item.saradomin_page_1")
                    add("item.saradomin_page_2")
                    add("item.saradomin_page_3")
                    add("item.saradomin_page_4")
                    add("item.guthix_page_1")
                    add("item.guthix_page_2")
                    add("item.guthix_page_3")
                    add("item.guthix_page_4")
                    add("item.ancient_page_1")
                    add("item.ancient_page_2")
                    add("item.ancient_page_3")
                    add("item.ancient_page_4")
                    add("item.armadyl_page_1")
                    add("item.armadyl_page_2")
                    add("item.armadyl_page_3")
                    add("item.armadyl_page_4")
                    add("item.bandos_page_1")
                    add("item.bandos_page_2")
                    add("item.bandos_page_3")
                    add("item.bandos_page_4")
                }
            val sharedFirelighter =
                weightedTable {
                    add("item.red_firelighter")
                    add("item.blue_firelighter")
                    add("item.green_firelighter")
                    add("item.white_firelighter")
                    add("item.purple_firelighter")
                }
            val runes =
                weightedTable {
                    add("item.nature_rune", amount = 30..50)
                    add("item.law_rune", amount = 30..50)
                    add("item.blood_rune", amount = 20..30)
                }
            val food =
                weightedTable {
                    add("item.shark_noted", amount = 12..15)
                    add("item.lobster_noted", amount = 12..15)
                }

            preroll {
                table(
                    rare,
                    numerator = 1,
                    denominator = 13,
                    asSubTable = true,
                    name = "Uniques",
                    icon = "item.robin_hood_hat",
                )
                table(
                    bow,
                    numerator = 1,
                    denominator = 270,
                    asSubTable = true,
                    name = "Bows",
                    icon = "item.magic_composite_bow",
                )
                table(sharedFirelighter, numerator = 1, denominator = 27)
                table(sharedScrolls, numerator = 1, denominator = 27)
                table(sharedPage, numerator = 1, denominator = 27)
            }
            main(17) {
                drop("item.coins", amount = 1000..5000, weight = 3)
                drop("item.purple_sweets", amount = 7..15, weight = 3)
                drop("item.coins", amount = 10000..15000, weight = 1)
                drop("item.purple_sweets", amount = 8..12, weight = 1)
                table(weaponsAndArmour, weight = 3)
                table(runes, weight = 3)
                table(food, weight = 3)
            }
        }.apply { name = "Hard clues" }
}
