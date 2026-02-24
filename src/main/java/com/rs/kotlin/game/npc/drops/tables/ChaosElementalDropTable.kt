package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object ChaosElementalDropTable {
    val table =
        dropTable(
            category = TableCategory.BOSS,
            rareTable = RareTableConfig(8, 128),
        ) {

            minorDrops(5) {
                drop("item.rocktail", amount = 2, weight = 1)
                drop("item.cooked_karambwan", amount = 3, weight = 1)
                drop("item.super_restore_4", amount = 1, weight = 1)
                drop("item.dragon_bones", amount = 1, weight = 1)
                drop("item.super_combat_potion_1", amount = 1, weight = 1)
            }

            preRollDrops {
                drop("item.dragon_pickaxe", numerator = 1, denominator = 256) {
                    collectionLog = true
                }
                listOf(
                    "item.statius_s_full_helm",
                    "item.statius_s_platebody",
                    "item.statius_s_platelegs",
                    "item.vesta_s_chainbody",
                    "item.vesta_s_plateskirt",
                    "item.vesta_s_longsword",
                    "item.vesta_s_spear",
                    "item.statius_s_warhammer",
                    "item.zuriel_s_hood",
                    "item.zuriel_s_robe_top",
                    "item.zuriel_s_robe_bottom",
                    "item.zuriel_s_staff",
                    "item.morrigan_s_coif",
                    "item.morrigan_s_leather_body",
                    "item.morrigan_s_leather_chaps",
                ).forEach {
                    drop(it, numerator = 1, denominator = 512) {
                        collectionLog = true
                        announce = true
                    }
                }

                listOf(
                    "item.corrupt_statius_s_full_helm",
                    "item.corrupt_statius_s_platebody",
                    "item.corrupt_statius_s_platelegs",
                    "item.corrupt_vesta_s_chainbody",
                    "item.corrupt_vesta_s_plateskirt",
                    "item.corrupt_vesta_s_longsword",
                    "item.corrupt_vesta_s_spear",
                    "item.corrupt_statius_s_warhammer",
                    "item.corrupt_zuriel_s_hood",
                    "item.corrupt_zuriel_s_robe_top",
                    "item.corrupt_zuriel_s_robe_bottom",
                    "item.corrupt_zuriel_s_staff",
                    "item.corrupt_morrigan_s_coif",
                    "item.corrupt_morrigan_s_leather_body",
                    "item.corrupt_morrigan_s_leather_chaps",
                ).forEach {
                    drop(it, numerator = 1, denominator = 256) {
                        collectionLog = true
                        announce = true
                    }
                }
            }

            mainDrops(128) {
                drop("item.rune_dart", amount = 100, weight = 5)
                drop("item.rune_platelegs", weight = 4)
                drop("item.rune_plateskirt", weight = 4)
                drop("item.rune_2h_sword", weight = 3)
                drop("item.rune_battleaxe", weight = 3)
                drop("item.rune_full_helm", weight = 3)
                drop("item.rune_kiteshield", weight = 3)
                drop("item.mystic_air_staff", weight = 3)
                drop("item.mystic_water_staff", weight = 3)
                drop("item.mystic_earth_staff", weight = 3)
                drop("item.mystic_fire_staff", weight = 3)
                drop("item.dragon_dagger", weight = 2)
                drop("item.dragon_2h_sword", weight = 2) {
                    collectionLog = true
                }
                drop("item.dragon_platelegs", weight = 2) {
                    collectionLog = true
                }
                drop("item.dragon_plateskirt", weight = 2) {
                    collectionLog = true
                }

                drop("item.chaos_rune", amount = 300..500, weight = 8)
                drop("item.blood_rune", amount = 100..250, weight = 8)
                drop("item.rune_arrow", amount = 150, weight = 5)

                drop("item.grimy_ranarr_noted", amount = 5..8, weight = 4)
                drop("item.grimy_snapdragon_noted", amount = 5..8, weight = 4)
                drop("item.grimy_avantoe_noted", amount = 5..8, weight = 3)
                drop("item.grimy_kwuarm_noted", amount = 5..8, weight = 3)

                drop("item.coal_noted", amount = 75..150, weight = 5)
                drop("item.mahogany_plank_noted", amount = 8..16, weight = 5)
                drop("item.rune_bar_noted", amount = 3..5, weight = 4)
                drop("item.adamant_bar_noted", amount = 8..12, weight = 4)

                drop("item.coins", amount = 20005..29995, weight = 7)
                drop("item.cooked_karambwan_noted", amount = 15..25, weight = 4)
                drop("item.rocktail_noted", amount = 15..25, weight = 4)
            }

            tertiaryDrops {
                drop(
                    "item.weapon_poison_++",
                    numerator = 10,
                    denominator = 128,
                )
                drop(
                    "item.weapon_poison_++",
                    numerator = 10,
                    denominator = 128,
                )
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 200,
                    condition = { player -> !player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "Chaos Elemental" }
}
