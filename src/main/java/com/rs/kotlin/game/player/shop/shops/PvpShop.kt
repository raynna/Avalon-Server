package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object PvpShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Pvp Token Store"
        currency = CurrencyType.PVP_TOKENS
        isGlobal = true

        item("item.dragon_claws", 10, 10, 1, 60000)
        item("item.armadyl_godsword", 10, 10, 1, 60000)
        item("item.korasi_sword", 10, 10, 1, 70000) // Korasi's sword
        item("item.vesta_s_chainbody", 10, 10, 1, 15000)
        item("item.vesta_s_plateskirt", 10, 10, 1, 15000)
        item("item.statius_s_full_helm", 10, 10, 1, 10000)
        item("item.statius_s_platebody", 10, 10, 1, 15000)
        item("item.statius_s_platelegs", 10, 10, 1, 15000)
        item("item.morrigan_s_coif", 10, 10, 1, 7500)
        item("item.morrigan_s_leather_body", 10, 10, 1, 10000)
        item("item.morrigan_s_leather_chaps", 10, 10, 1, 10000)
        item("item.zuriel_s_hood", 10, 10, 1, 7500)
        item("item.zuriel_s_robe_top", 10, 10, 1, 10000)
        item("item.zuriel_s_robe_bottom", 10, 10, 1, 10000)
        item("item.vesta_s_longsword", 10, 10, 1, 22000)
        item("item.vesta_s_spear", 10, 10, 1, 12000)
        item("item.statius_s_warhammer", 10, 10, 1, 16000)
        item("item.ancestral_hat", 10, 10, 1, 20000)
        item("item.ancestral_robe_top", 10, 10, 1, 40000)
        item("item.ancestral_robe_bottoms", 10, 10, 1, 40000)
        item("item.kodai_wand", 10, 10, 1, 25000)
        item("item.scythe_of_vitur", 10, 10, 1, 80000)
        item("item.twisted_bow", 10, 10, 1, 80000)
        item("item.dragon_hunter_crossbow", 10, 10, 1, 60000)
        item("item.dragon_hunter_lance", 10, 10, 1, 60000)
        item("item.nightmare_staff", 10, 10, 1, 20000)
        item("item.volatile_orb", 10, 10, 1, 20000)
        item("item.harmonised_orb", 10, 10, 1, 20000)
        item("item.eldritch_orb", 10, 10, 1, 20000)
        item("item.elder_maul", 10, 10, 1, 20000)
        item("item.sanguinesti_staff", 10, 10, 1, 20000)
        item("item.neitiznot_faceguard", 10, 10, 1, 20000)
        item("item.amulet_of_torture", 10, 10, 1, 20000)
        item("item.necklace_of_anguish", 10, 10, 1, 20000)
        item("item.tormented_bracelet", 10, 10, 1, 20000)
        item("item.crystal_helm", 10, 10, 1, 10000)
        item("item.crystal_body", 10, 10, 1, 20000)
        item("item.crystal_legs", 10, 10, 1, 15000)
        item("item.bow_of_faerdhinen", 10, 10, 1, 30000)
        item("item.thammaron_s_sceptre", 10, 10, 1, 30000)
        item("item.craw_s_bow", 10, 10, 1, 30000)
        item("item.viggora_s_chainmace", 10, 10, 1, 30000)
        item("item.zaryte_crossbow", 10, 10, 1, 30000)
        item("item.noxious_halberd", 10, 10, 1, 30000)
        item("item.amulet_of_rancour", 10, 10, 1, 30000)
    }
}
