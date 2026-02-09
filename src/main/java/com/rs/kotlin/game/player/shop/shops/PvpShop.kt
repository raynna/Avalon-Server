package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object PvpShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Pvp Token Store"
        currency = CurrencyType.PVP_TOKENS
        isGlobal = true

        item("item.dragon_claws", 1, 10, 1, 60000)
        item("item.saradomin_godsword", 1, 10, 1, 30000)
        item("item.zamorak_godsword", 1, 10, 1, 30000)
        item("item.bandos_godsword", 1, 10, 1, 30000)
        item("item.armadyl_godsword", 1, 10, 1, 60000)
		item("item.ancient_godsword", 1, 10, 1, 50000)
        item("item.korasi_sword", 1, 10, 1, 70000) // Korasi's sword
        item("item.vesta_s_chainbody", 1, 10, 1, 15000)
        item("item.vesta_s_plateskirt", 1, 10, 1, 15000)
        item("item.statius_s_full_helm", 1, 10, 1, 10000)
        item("item.statius_s_platebody", 1, 10, 1, 15000)
        item("item.statius_s_platelegs", 1, 10, 1, 15000)
        item("item.morrigan_s_coif", 1, 10, 1, 7500)
        item("item.morrigan_s_leather_body", 1, 10, 1, 10000)
        item("item.morrigan_s_leather_chaps", 1, 10, 1, 10000)
        item("item.zuriel_s_hood", 1, 10, 1, 7500)
        item("item.zuriel_s_robe_top", 1, 10, 1, 10000)
        item("item.zuriel_s_robe_bottom", 1, 10, 1, 10000)
        item("item.vesta_s_longsword", 1, 10, 1, 22000)
        item("item.vesta_s_spear", 1, 10, 1, 12000)
        item("item.statius_s_warhammer", 1, 10, 1, 16000)
        item("item.ancestral_hat", 1, 10, 1, 20000)
        item("item.ancestral_robe_top", 1, 10, 1, 40000)
        item("item.ancestral_robe_bottoms", 1, 10, 1, 40000)
        item("item.kodai_wand", 1, 10, 1, 25000)
        item("item.scythe_of_vitur", 1, 10, 1, 80000)
        item("item.twisted_bow", 1, 10, 1, 80000)
        item("item.dragon_hunter_crossbow", 1, 10, 1, 60000)
        item("item.dragon_hunter_lance", 1, 10, 1, 60000)
        item("item.nightmare_staff", 1, 10, 1, 20000)
        item("item.volatile_orb", 1, 10, 1, 20000)
        item("item.harmonised_orb", 1, 10, 1, 20000)
        item("item.eldritch_orb", 1, 10, 1, 20000)
        item("item.elder_maul", 1, 10, 1, 20000)
        item("item.sanguinesti_staff", 1, 10, 1, 20000)
        item("item.neitiznot_faceguard", 1, 10, 1, 20000)
        item("item.amulet_of_torture", 1, 10, 1, 20000)
        item("item.necklace_of_anguish", 1, 10, 1, 20000)
        item("item.tormented_bracelet", 1, 10, 1, 20000)
        item("item.crystal_helm", 1, 10, 1, 10000)
        item("item.crystal_body", 1, 10, 1, 20000)
        item("item.crystal_legs", 1, 10, 1, 15000)
        item("item.bow_of_faerdhinen", 1, 10, 1, 30000)
        item("item.thammaron_s_sceptre", 1, 10, 1, 30000)
        item("item.craw_s_bow", 1, 10, 1, 30000)
        item("item.viggora_s_chainmace", 1, 10, 1, 30000)
        item("item.zaryte_crossbow", 1, 10, 1, 30000)
        item("item.noxious_halberd", 1, 10, 1, 30000)
        item("item.amulet_of_rancour", 1, 10, 1, 30000)
    }
}
