package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable

object HardTreasureTrailTable {
    val table =
        dropTable(sourceAction = "opening", category = TableCategory.CLUE) {
            val potion =
                alwaysTable {
                    add("item.super_attack_4_noted", amount = 5..5)
                    add("item.super_strength_4_noted", amount = 5..5)
                    add("item.super_defence_4_noted", amount = 5..5)
                }
            val thirdAge =
                weightedTable {
                    add("item.third_age_full_helmet")
                    add("item.third_age_platebody")
                    add("item.third_age_platelegs")
                }
            val gilded =
                weightedTable {
                    add("item.gilded_platebody")
                }
            val mega =
                weightedTable(7) {
                    table(potion, weight = 1, asSubTable = true, name = "Potions", icon = "item.super_attack_4")
                    table(thirdAge, weight = 1, asSubTable = true, name = "Third age", icon = "item.third_age_full_helmet")
                    table(gilded, weight = 5, asSubTable = true, name = "Gilded", icon = "item.gilded_platebody")
                }
            val heraldic =
                weightedTable {
                    add("item.rune_platebody_h1")
                    add("item.rune_platebody_h2")
                    add("item.rune_platebody_h3")
                    add("item.rune_platebody_h4")
                    add("item.rune_platebody_h5")
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

                    add("item.armadyl_full_helm")
                    add("item.armadyl_platebody")
                    add("item.armadyl_platelegs")
                    add("item.armadyl_plateskirt")
                    add("item.armadyl_kiteshield")
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
                    add("item.rune_platebody", amount = 30..50)
                    add("item.law_rune", amount = 30..50)
                    add("item.blood_rune", amount = 20..30)
                }
            val food =
                weightedTable {
                    add("item.shark_noted", amount = 12..15)
                    add("item.lobster_noted", amount = 12..15)
                }

            preroll {
                table(rare, numerator = 1, denominator = 13, asSubTable = true, name = "Uniques", icon = "item.robin_hood_hat")
                table(bow, numerator = 1, denominator = 270, asSubTable = true, name = "Bows", icon = "item.magic_composite_bow")
                table(sharedFirelighter, numerator = 1, denominator = 27)
                table(sharedScrolls, numerator = 1, denominator = 27)
                table(sharedPage, numerator = 1, denominator = 27)
            }
            main(18) {
                table(weaponsAndArmour, weight = 13)
                table(runes, weight = 3)
                table(food, weight = 2)
            }
        }.apply { name = "Hard clues" }
}
