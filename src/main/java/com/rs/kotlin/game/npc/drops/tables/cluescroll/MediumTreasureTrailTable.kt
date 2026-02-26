package com.rs.kotlin.game.npc.drops.tables.cluescroll

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable

object MediumTreasureTrailTable {
    val table =
        dropTable(sourceAction = "opening", category = TableCategory.CLUE) {
            val elegant =
                weightedTable(meta = { collectionLog = true }) {
                    add("item.purple_elegant_shirt")
                    add("item.purple_elegant_blouse")
                    add("item.purple_elegant_legs")
                    add("item.purple_elegant_skirt")
                }
            val heraldic =
                weightedTable(meta = { collectionLog = true }) {
                    add("item.adamant_helm_h1")
                    add("item.adamant_helm_h2")
                    add("item.adamant_helm_h3")
                    add("item.adamant_helm_h4")
                    add("item.adamant_helm_h5")
                    add("item.adamant_platebody_h1")
                    add("item.adamant_platebody_h2")
                    add("item.adamant_platebody_h3")
                    add("item.adamant_platebody_h4")
                    add("item.adamant_platebody_h5")
                    add("item.adamant_platelegs_h1")
                    add("item.adamant_platelegs_h2")
                    add("item.adamant_platelegs_h3")
                    add("item.adamant_platelegs_h4")
                    add("item.adamant_platelegs_h5")
                    add("item.adamant_plateskirt_h1")
                    add("item.adamant_plateskirt_h2")
                    add("item.adamant_plateskirt_h3")
                    add("item.adamant_plateskirt_h4")
                    add("item.adamant_plateskirt_h5")
                    add("item.adamant_shield_h1")
                    add("item.adamant_shield_h2")
                    add("item.adamant_shield_h3")
                    add("item.adamant_shield_h4")
                    add("item.adamant_shield_h5")
                }
            val weaponsAndArmour =
                weightedTable {
                    add("item.adamant_full_helm")
                    add("item.adamant_platebody")
                    add("item.adamant_platelegs")
                    add("item.adamant_plateskirt")
                    add("item.adamant_longsword")
                    add("item.adamant_dagger")
                    add("item.adamant_battleaxe")
                    add("item.adamant_hatchet")
                    add("item.adamant_pickaxe")
                    add("item.green_d_hide_body")
                    add("item.green_d_hide_chaps")
                    add("item.yew_shortbow")
                    add("item.fire_battlestaff")
                    add("item.yew_longbow")
                    add("item.amulet_of_power")
                }
            val unique =
                weightedTable(meta = { collectionLog = true }) {
                    add("item.strength_amulet_t", weight = 4)
                    add("item.ranger_boots", weight = 2)
                    add("item.wizard_boots", weight = 2)

                    add("item.adamant_full_helm_t", weight = 2)
                    add("item.adamant_platebody_t", weight = 2)
                    add("item.adamant_platelegs_t", weight = 2)
                    add("item.adamant_plateskirt_t", weight = 2)
                    add("item.adamant_kiteshield_t", weight = 2)

                    add("item.adamant_full_helm_g", weight = 2)
                    add("item.adamant_platebody_g", weight = 2)
                    add("item.adamant_platelegs_g", weight = 2)
                    add("item.adamant_plateskirt_g", weight = 2)
                    add("item.adamant_kiteshield_g", weight = 2)
                    add("item.green_d_hide_body_g", weight = 2)
                    add("item.green_d_hide_chaps_g", weight = 2)
                    add("item.green_d_hide_body_t", weight = 2)
                    add("item.green_d_hide_chaps_t", weight = 2)

                    add("item.saradomin_mitre", weight = 2)
                    add("item.saradomin_cloak", weight = 2)
                    add("item.guthix_mitre", weight = 2)
                    add("item.guthix_cloak", weight = 2)
                    add("item.zamorak_mitre", weight = 2)
                    add("item.zamorak_cloak", weight = 2)
                    add("item.ancient_mitre", weight = 2)
                    add("item.ancient_cloak", weight = 2)
                    add("item.armadyl_mitre", weight = 2)
                    add("item.armadyl_cloak", weight = 2)
                    add("item.bandos_mitre", weight = 2)
                    add("item.bandos_cloak", weight = 2)
                    add("item.red_boater", weight = 2)
                    add("item.green_boater", weight = 2)
                    add("item.orange_boater", weight = 2)
                    add("item.black_boater", weight = 2)
                    add("item.blue_boater", weight = 2)
                    add("item.red_headband", weight = 2)
                    add("item.black_headband", weight = 2)
                    add("item.brown_headband", weight = 2)

                    table(heraldic, weight = 2, asSubTable = true, name = "Heralic", icon = "item.adamant_platebody_h5")
                    table(elegant, weight = 1, asSubTable = true, name = "Elegant", icon = "item.purple_elegant_shirt")
                }
            val bow =
                weightedTable(10) {
                    add("item.yew_composite_bow", weight = 1) { collectionLog = true }
                    add("item.yew_longbow", weight = 9)
                }
            val runes =
                weightedTable {
                    add("item.air_rune", amount = 50..100)
                    add("item.mind_rune", amount = 50..100)
                    add("item.water_rune", amount = 50..100)
                    add("item.earth_rune", amount = 50..100)
                    add("item.fire_rune", amount = 50..100)
                    add("item.chaos_rune", amount = 10..20)
                    add("item.nature_rune", amount = 10..20)
                    add("item.law_rune", amount = 10..20)
                    add("item.death_rune", amount = 10..20)
                }
            val food =
                weightedTable {
                    add("item.lobster_noted", amount = 8..12)
                    add("item.swordfish_noted", amount = 8..12)
                }

            preroll {
                table(
                    unique,
                    numerator = 1,
                    denominator = 10,
                    asSubTable = true,
                    name = "Uniques",
                    icon = "item.ranger_boots",
                )
                table(
                    bow,
                    numerator = 1,
                    denominator = 270,
                    asSubTable = true,
                    name = "Bows",
                    icon = "item.yew_composite_bow",
                )
                table(SharedClueScroll.firelighters, numerator = 1, denominator = 27)
                table(SharedClueScroll.scrolls, numerator = 1, denominator = 27)
                table(SharedClueScroll.pages, numerator = 1, denominator = 27)
            }
            main(17) {
                drop("item.coins", amount = 200..1000, weight = 3)
                drop("item.purple_sweets", amount = 5..10, weight = 3)
                drop("item.coins", amount = 10000..15000, weight = 1)
                drop("item.purple_sweets", amount = 8..12, weight = 1)
                table(weaponsAndArmour, weight = 3)
                table(runes, weight = 3)
                table(food, weight = 3)
            }
        }.apply {
            name = "Medium clues"
        }
}
