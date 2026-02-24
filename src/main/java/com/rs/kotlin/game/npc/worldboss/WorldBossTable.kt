package com.rs.kotlin.game.npc.worldboss

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable

object WorldBossTable {
    val regular =
        dropTable(category = TableCategory.BOSS, rolls = 3) {
            alwaysDrops {
                drop("item.coins", amount = 250000..500000)
                drop("item.pvp_token", amount = 500..1_500) {
                    collectionLog = true
                }
            }

            preRollDrops {
                listOf(
                    "statius_s_platebody",
                    "statius_s_platelegs",
                    "statius_s_full_helm",
                    "vesta_s_chainbody",
                    "vesta_s_plateskirt",
                    "vesta_s_longsword",
                    "vesta_s_spear",
                    "zuriel_s_hood",
                    "zuriel_s_robe_top",
                    "zuriel_s_robe_bottom",
                    "zuriel_s_staff",
                    "morrigan_s_coif",
                    "morrigan_s_leather_body",
                    "morrigan_s_leather_chaps",
                ).forEach {
                    drop("item.$it", numerator = 1, denominator = 256) {
                        collectionLog = true
                    }
                }

                listOf(
                    "corrupt_statius_s_platebody",
                    "corrupt_statius_s_platelegs",
                    "corrupt_statius_s_full_helm",
                    "corrupt_vesta_s_chainbody",
                    "corrupt_vesta_s_plateskirt",
                    "corrupt_vesta_s_longsword",
                    "corrupt_vesta_s_spear",
                    "corrupt_zuriel_s_hood",
                    "corrupt_zuriel_s_robe_top",
                    "corrupt_zuriel_s_robe_bottom",
                    "corrupt_zuriel_s_staff",
                    "corrupt_morrigan_s_coif",
                    "corrupt_morrigan_s_leather_body",
                    "corrupt_morrigan_s_leather_chaps",
                ).forEach {
                    drop("item.$it", numerator = 1, denominator = 129) {
                        collectionLog = true
                    }
                }
            }

            mainDrops(512) {

                drop("item.saradomin_brew_4", amount = 3..5, weight = 55)
                drop("item.super_restore_4", amount = 3..5, weight = 55)
                drop("item.rocktail", amount = 5..10, weight = 55)
                drop("item.overload_4", amount = 2..4, weight = 10)

                drop("item.broken_statue_headdress", weight = 14)
                drop("item.seren_statuette", weight = 9)
                drop("item.zamorak_statuette", weight = 9)
                drop("item.saradomin_statuette", weight = 9)
                drop("item.bandos_statuette", weight = 9)
                drop("item.armadyl_statuette", weight = 7)
                drop("item.ruby_chalice", weight = 7)
                drop("item.guthixian_brazier", weight = 7)
                drop("item.armadyl_totem", weight = 5)
                drop("item.zamorak_medallion", weight = 5)
                drop("item.saradomin_carving", weight = 5)
                drop("item.bandos_scrimshaw", weight = 5)
                drop("item.saradomin_amphora", weight = 4)
                drop("item.ancient_psaltery_bridge", weight = 4)
                drop("item.bronzed_dragon_claw", weight = 3)
                drop("item.third_age_carafe", weight = 3)
                drop("item.ancient_statuette", weight = 2)

                drop("item.abyssal_whip", weight = 10)
                drop("item.saradomin_sword", weight = 10)
                drop("item.staff_of_light", weight = 10)
                drop("item.amulet_of_fury", weight = 10)
                drop("item.berserker_ring", weight = 10)
                drop("item.seers_ring", weight = 10)
                drop("item.archers_ring", weight = 10)
                drop("item.warrior_ring", weight = 10)
                drop("item.dragon_scimitar", weight = 10)
                drop("item.dragon_boots", weight = 10)
                drop("item.magic_chest", weight = 6) { collectionLog = true }

                listOf(
                    "ahrim_s_hood",
                    "ahrim_s_robe_top",
                    "ahrim_s_robe_skirt",
                    "ahrim_s_staff",
                    "dharok_s_helm",
                    "dharok_s_platebody",
                    "dharok_s_platelegs",
                    "dharok_s_greataxe",
                    "guthan_s_helm",
                    "guthan_s_platebody",
                    "guthan_s_chainskirt",
                    "guthan_s_warspear",
                    "karil_s_coif",
                    "karil_s_top",
                    "karil_s_skirt",
                    "karil_s_crossbow",
                    "torag_s_helm",
                    "torag_s_platebody",
                    "torag_s_platelegs",
                    "torag_s_hammers",
                    "verac_s_helm",
                    "verac_s_brassard",
                    "verac_s_plateskirt",
                    "verac_s_flail",
                ).forEach { drop("item.$it", weight = 10) }
            }
        }.apply {
            name = "World Boss Regular"
            collectionGroup = "World Boss"
        }

    val chest =
        dropTable(sourceAction = "opening", category = TableCategory.OTHER, rolls = 1) {
            alwaysDrops {
                drop("item.coins", amount = 500_000..1_500_000)
                drop("item.pvp_token", amount = 1_000..3_000) { collectionLog = true }
            }
            mainDrops(512) {

                drop("item.abyssal_whip", weight = 8) { collectionLog = true }
                drop("item.staff_of_light", weight = 8) { collectionLog = true }
                drop("item.amulet_of_fury", weight = 8) { collectionLog = true }
                drop("item.berserker_ring", weight = 8) { collectionLog = true }
                drop("item.seers_ring", weight = 6) { collectionLog = true }
                drop("item.archers_ring", weight = 6) { collectionLog = true }
                drop("item.warrior_ring", weight = 6) { collectionLog = true }

                listOf(
                    "statius_s_platebody",
                    "statius_s_platelegs",
                    "statius_s_full_helm",
                    "vesta_s_chainbody",
                    "vesta_s_plateskirt",
                    "vesta_s_longsword",
                    "vesta_s_spear",
                    "zuriel_s_robe_top",
                    "zuriel_s_robe_bottom",
                    "zuriel_s_staff",
                    "morrigan_s_leather_body",
                    "morrigan_s_leather_chaps",
                ).forEach { drop("item.$it", weight = 3) { collectionLog = true } }

                listOf(
                    "corrupt_statius_s_platebody",
                    "corrupt_statius_s_platelegs",
                    "corrupt_statius_s_full_helm",
                    "corrupt_vesta_s_chainbody",
                    "corrupt_vesta_s_plateskirt",
                    "corrupt_vesta_s_longsword",
                    "corrupt_vesta_s_spear",
                    "corrupt_zuriel_s_robe_top",
                    "corrupt_zuriel_s_robe_bottom",
                    "corrupt_zuriel_s_staff",
                    "corrupt_morrigan_s_leather_body",
                    "corrupt_morrigan_s_leather_chaps",
                ).forEach { drop("item.$it", weight = 5) { collectionLog = true } }

                listOf(
                    "ahrim_s_hood",
                    "ahrim_s_robe_top",
                    "ahrim_s_robe_skirt",
                    "ahrim_s_staff",
                    "dharok_s_helm",
                    "dharok_s_platebody",
                    "dharok_s_platelegs",
                    "dharok_s_greataxe",
                    "guthan_s_helm",
                    "guthan_s_platebody",
                    "guthan_s_chainskirt",
                    "guthan_s_warspear",
                    "karil_s_coif",
                    "karil_s_top",
                    "karil_s_skirt",
                    "karil_s_crossbow",
                    "torag_s_helm",
                    "torag_s_platebody",
                    "torag_s_platelegs",
                    "torag_s_hammers",
                    "verac_s_helm",
                    "verac_s_brassard",
                    "verac_s_plateskirt",
                    "verac_s_flail",
                ).forEach { drop("item.$it", weight = 7) }

                drop("item.spirit_shield", weight = 6) { collectionLog = true }
                drop("item.blessed_spirit_shield", weight = 3) { collectionLog = true }
                drop("item.arcane_spirit_shield", weight = 1) { collectionLog = true }
                drop("item.spectral_spirit_shield", weight = 1) { collectionLog = true }
                drop("item.divine_spirit_shield", weight = 1) { collectionLog = true }
                drop("item.elysian_spirit_shield", weight = 1) { collectionLog = true }

                listOf(
                    "armadyl_helmet",
                    "armadyl_chestplate",
                    "armadyl_chainskirt",
                    "bandos_chestplate",
                    "bandos_tassets",
                    "bandos_boots",
                ).forEach { drop("item.$it", weight = 4) { collectionLog = true } }

                listOf(
                    "torva_full_helm",
                    "torva_platebody",
                    "torva_platelegs",
                    "pernix_cowl",
                    "pernix_body",
                    "pernix_chaps",
                    "virtus_mask",
                    "virtus_robe_top",
                    "virtus_robe_legs",
                    "ancestral_hat",
                    "ancestral_robe_top",
                    "ancestral_robe_bottoms",
                    "scythe_of_vitur",
                    "twisted_bow",
                    "dragon_hunter_crossbow",
                    "dragon_hunter_lance",
                    "kodai_wand",
                    "nightmare_staff",
                    "volatile_orb",
                    "harmonised_orb",
                    "eldritch_orb",
                    "amulet_of_rancour",
                ).forEach { drop("item.$it", weight = 1) { collectionLog = true } }

                drop("item.elder_maul", weight = 2) { collectionLog = true }
                drop("item.amulet_of_torture", weight = 2) { collectionLog = true }
                drop("item.necklace_of_anguish", weight = 2) { collectionLog = true }
                drop("item.tormented_bracelet", weight = 2) { collectionLog = true }
                drop("item.bow_of_faerdhinen", weight = 2) { collectionLog = true }
                drop("item.noxious_halberd", weight = 2) { collectionLog = true }
                drop("item.neitiznot_faceguard", weight = 2) { collectionLog = true }
                drop("item.toxic_staff_uncharged", weight = 2) { collectionLog = true }

                listOf(
                    "thammaron_s_sceptre",
                    "craw_s_bow",
                    "viggora_s_chainmace",
                ).forEach { drop("item.$it", weight = 2) { collectionLog = true } }

                listOf("crystal_helm", "crystal_body", "crystal_legs")
                    .forEach { drop("item.$it", weight = 5) { collectionLog = true } }

                drop("item.ancient_statuette", weight = 30) { collectionLog = true }
            }
        }.apply {
            name = "World Boss Chest"
            collectionGroup = "Magic Chest"
        }
}
