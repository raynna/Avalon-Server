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
                weightedTable(1) {
                    add("item.third_age_full_helmet")
                    add("item.third_age_platebody")
                    add("item.third_age_platelegs")
                }
            val gilded =
                weightedTable(1) {
                    add("item.gilded_platebody")
                }
            val mega =
                weightedTable(7) {
                    table(potion, weight = 1, asSubTable = true, name = "Potions", icon = "item.super_attack_4")
                    table(thirdAge, weight = 1, asSubTable = true, name = "Third age", icon = "item.third_age_full_helmet")
                    table(gilded, weight = 5, asSubTable = true, name = "Gilded", icon = "item.gilded_platebody")
                }
            val heraldic =
                weightedTable(5) {
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
                weightedTable(7) {
                    add("item.robin_hood_hat")
                    table(heraldic, weight = 5, asSubTable = true, name = "Heralic", icon = "item.rune_platebody_h5")
                    table(mega, weight = 1)
                }
            val bow =
                weightedTable(10) {
                    add("item.magic_composite_bow", weight = 1)
                    add("item.magic_longbow", weight = 9)
                }
            val sharedScrolls =
                weightedTable(total = 3) {
                    // unknown
                    add("item.lumber_yard_teleport", amount = 5..15)
                    add("item.nardah_teleport", amount = 5..15)
                    add("item.bandit_camp_teleport", amount = 5..15)
                }
            val sharedPage =
                weightedTable(4) {
                    add("item.zamorak_page_1")
                    add("item.zamorak_page_2")
                    add("item.zamorak_page_3")
                    add("item.zamorak_page_4")
                }
            val sharedFirelighter =
                weightedTable(5) {
                    add("item.red_firelighter")
                    add("item.blue_firelighter")
                    add("item.green_firelighter")
                    add("item.white_firelighter")
                    add("item.purple_firelighter")
                }
            val runes =
                weightedTable(3) {
                    add("item.rune_platebody", amount = 30..50)
                    add("item.law_rune", amount = 30..50)
                    add("item.blood_rune", amount = 20..30)
                }
            val food =
                weightedTable(2) {
                    add("item.shark_noted", amount = 12..15)
                    add("item.lobster_noted", amount = 12..15)
                }

            preroll(2708) {
                table(rare, weight = 13, asSubTable = true, name = "Uniques")
                table(bow, weight = 10, asSubTable = true, name = "Bows")
                table(sharedFirelighter, weight = 27)
                table(sharedScrolls, weight = 27)
                table(sharedPage, weight = 27)
            }
            main(18) {
                table(weaponsAndArmour, weight = 13)
                table(runes, weight = 3)
                table(food, weight = 2)
            }
        }.apply { name = "Hard clues" }
}
