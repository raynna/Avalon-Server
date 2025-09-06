package com.rs.kotlin.game.npc.worldboss

import com.rs.kotlin.game.npc.drops.dropTable

object WorldBossTable {

    val regular = dropTable(rolls = 3) {
        alwaysDrops {
            drop("item.coins", amount = 250000..500000)
        }

        mainDrops(512) {
            drop("item.saradomin_brew_4_noted", amount = 5..15, weight = 68)
            drop("item.super_restore_4_noted", amount = 5..15, weight = 68)
            drop("item.rocktail_noted", amount = 25..50, weight = 68)
            drop("item.overload_4", amount = 2..5, weight = 12)

            drop("item.broken_statue_headdress", weight = 16)
            drop("item.seren_statuette", weight = 10)
            drop("item.zamorak_statuette", weight = 10)
            drop("item.saradomin_statuette", weight = 10)
            drop("item.bandos_statuette", weight = 10)
            drop("item.armadyl_statuette", weight = 8)
            drop("item.ruby_chalice", weight = 8)
            drop("item.guthixian_brazier", weight = 8)
            drop("item.armadyl_totem", weight = 6)
            drop("item.zamorak_medallion", weight = 6)
            drop("item.saradomin_carving", weight = 6)
            drop("item.bandos_scrimshaw", weight = 6)
            drop("item.saradomin_amphora", weight = 4)
            drop("item.ancient_psaltery_bridge", weight = 4)
            drop("item.bronzed_dragon_claw", weight = 3)
            drop("item.third_age_carafe", weight = 3)
            drop("item.ancient_statuette", weight = 2)

            drop("item.abyssal_whip", weight = 12)
            drop("item.saradomin_sword", weight = 12)
            drop("item.staff_of_light", weight = 12)
            drop("item.amulet_of_fury", weight = 12)
            drop("item.berserker_ring", weight = 12)
            drop("item.seers_ring", weight = 12)
            drop("item.archers_ring", weight = 12)
            drop("item.warrior_ring", weight = 12)
            drop("item.dragon_scimitar", weight = 12)
            drop("item.dragon_boots", weight = 12)

            listOf(
                "ahrim_s_hood", "ahrim_s_robe_top", "ahrim_s_robe_skirt", "ahrim_s_staff",
                "dharok_s_helm", "dharok_s_platebody", "dharok_s_platelegs", "dharok_s_greataxe",
                "guthan_s_helm", "guthan_s_platebody", "guthan_s_chainskirt", "guthan_s_warspear",
                "karil_s_coif", "karil_s_top", "karil_s_skirt", "karil_s_crossbow",
                "torag_s_helm", "torag_s_platebody", "torag_s_platelegs", "torag_s_hammers",
                "verac_s_helm", "verac_s_brassard", "verac_s_plateskirt", "verac_s_flail"
            ).forEach { drop("item.$it", weight = 12) }

            drop("item.dragon_claws", weight = 2)
            drop("item.armadyl_godsword", weight = 2)
            drop("item.bandos_godsword", weight = 2)
            drop("item.saradomin_godsword", weight = 2)
            drop("item.statius_s_platebody", weight = 1)
            drop("item.statius_s_platelegs", weight = 1)
            drop("item.vesta_s_chainbody", weight = 1)
            drop("item.zuriel_s_staff", weight = 1)
            drop("item.morrigan_s_leather_body", weight = 1)
            drop("item.corrupt_statius_s_platebody", weight = 1)
            drop("item.corrupt_vesta_s_longsword", weight = 1)
            drop("item.magic_chest", weight = 9) // ~2% chance
        }
    }
    .apply { name = "World Boss Regular" }

    val chest = dropTable(rolls = 1) {
        mainDrops(512) {
            drop("item.broken_statue_headdress", weight = 40)
            drop("item.seren_statuette", weight = 20)
            drop("item.zamorak_statuette", weight = 20)
            drop("item.saradomin_statuette", weight = 20)
            drop("item.bandos_statuette", weight = 20)
            drop("item.armadyl_statuette", weight = 10)
            drop("item.ruby_chalice", weight = 10)
            drop("item.guthixian_brazier", weight = 10)
            drop("item.armadyl_totem", weight = 6)
            drop("item.zamorak_medallion", weight = 6)
            drop("item.saradomin_carving", weight = 6)
            drop("item.bandos_scrimshaw", weight = 6)
            drop("item.saradomin_amphora", weight = 5)
            drop("item.ancient_psaltery_bridge", weight = 5)

            drop("item.abyssal_whip", weight = 10)
            drop("item.staff_of_light", weight = 10)
            drop("item.amulet_of_fury", weight = 10)
            drop("item.berserker_ring", weight = 10)
            drop("item.seers_ring", weight = 8)
            drop("item.archers_ring", weight = 8)
            drop("item.warrior_ring", weight = 8)

            listOf(
                "statius_s_platebody", "statius_s_platelegs", "statius_s_full_helm",
                "vesta_s_chainbody", "vesta_s_plateskirt", "vesta_s_longsword", "vesta_s_spear",
                "zuriel_s_robe_top", "zuriel_s_robe_bottom", "zuriel_s_staff",
                "morrigan_s_leather_body", "morrigan_s_leather_chaps"
            ).forEach { drop("item.$it", weight = 2) }

            listOf(
                "corrupt_statius_s_platebody", "corrupt_statius_s_platelegs", "corrupt_statius_s_full_helm",
                "corrupt_vesta_s_chainbody", "corrupt_vesta_s_plateskirt", "corrupt_vesta_s_longsword", "corrupt_vesta_s_spear",
                "corrupt_zuriel_s_robe_top", "corrupt_zuriel_s_robe_bottom", "corrupt_zuriel_s_staff",
                "corrupt_morrigan_s_leather_body", "corrupt_morrigan_s_leather_chaps"
            ).forEach { drop("item.$it", weight = 4) }

            listOf(
                "ahrim_s_hood", "ahrim_s_robe_top", "ahrim_s_robe_skirt", "ahrim_s_staff",
                "dharok_s_helm", "dharok_s_platebody", "dharok_s_platelegs", "dharok_s_greataxe",
                "guthan_s_helm", "guthan_s_platebody", "guthan_s_chainskirt", "guthan_s_warspear",
                "karil_s_coif", "karil_s_top", "karil_s_skirt", "karil_s_crossbow",
                "torag_s_helm", "torag_s_platebody", "torag_s_platelegs", "torag_s_hammers",
                "verac_s_helm", "verac_s_brassard", "verac_s_plateskirt", "verac_s_flail"
            ).forEach { drop("item.$it", weight = 6) }

            drop("item.spirit_shield", weight = 4)
            drop("item.blessed_spirit_shield", weight = 2)
            drop("item.arcane_spirit_shield", weight = 1)
            drop("item.spectral_spirit_shield", weight = 1)
            drop("item.divine_spirit_shield", weight = 1)
            drop("item.elysian_spirit_shield", weight = 1)
            drop("item.armadyl_helmet", weight = 3)
            drop("item.armadyl_chestplate", weight = 3)
            drop("item.armadyl_chainskirt", weight = 3)
            drop("item.bandos_chestplate", weight = 3)
            drop("item.bandos_tassets", weight = 3)
            drop("item.bandos_boots", weight = 3)

            drop("item.torva_full_helm", weight = 1)
            drop("item.torva_platebody", weight = 1)
            drop("item.torva_platelegs", weight = 1)
            drop("item.pernix_cowl", weight = 1)
            drop("item.pernix_body", weight = 1)
            drop("item.pernix_chaps", weight = 1)
            drop("item.virtus_mask", weight = 1)
            drop("item.virtus_robe_top", weight = 1)
            drop("item.virtus_robe_legs", weight = 1)
            drop("item.ancient_statuette_noted", weight = 19)
        }
    }.apply { name = "World Boss Chest"}
    }

