package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object RangeShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Ranged Equipment Store"
        currency = CurrencyType.COINS
        isGlobal = true

        item("item.coif")
        item("item.leather_body")
        item("item.hardleather_body")
        item("item.studded_body")
        item("item.green_d_hide_body")
        item("item.green_d_hide_chaps")
        item("item.green_d_hide_vambraces")

        item("item.archer_helm")
        item("item.black_d_hide_body")
        item("item.black_d_hide_chaps")
        item("item.black_d_hide_vambraces")
        item("item.black_d_hide_body_g")
        item("item.black_d_hide_body_t")
        item("item.black_d_hide_chaps_g")
        item("item.black_d_hide_chaps_t")
        item("item.armadyl_helmet")
        item("item.armadyl_chestplate")
        item("item.armadyl_chainskirt")

        item("item.snakeskin_bandana")
        item("item.snakeskin_body")
        item("item.snakeskin_chaps")
        item("item.snakeskin_boots")
        item("item.karil_s_coif")
        item("item.karil_s_top")
        item("item.karil_s_skirt")

        item("item.royal_d_hide_body")
        item("item.royal_d_hide_chaps")

        item("item.ranger_boots")
        item("item.robin_hood_hat")
        item("item.glaiven_boots")
        item("item.iron_knife", 10)
        item("item.rune_knife", 10)
        item("item.dragon_dart", 10)
        item("item.toktz_xil_ul", 10)

        item("item.shortbow")
        item("item.yew_shortbow")
        item("item.magic_shortbow")
        item("item.rune_crossbow")
        item("item.dragon_crossbow")
        item("item.iron_arrow", 10)
        item("item.adamant_arrow", 10)
        item("item.rune_arrow", 10)
        item("item.amethyst_arrow", 10)

        item("item.dragon_arrow", 5)
        item("item.ruby_bolts_e", 10)
        item("item.diamond_bolts_e", 10)
        item("item.dragonstone_bolts_e", 10)
        item("item.onyx_bolts_e", 10)
        item("item.ruby_dragon_bolts_e", 10)
        item("item.diamond_dragon_bolts_e", 10)
        item("item.dragonstone_dragon_bolts_e", 10)
        item("item.opal_dragon_bolts_e", 10)
        item("item.onyx_dragon_bolts_e", 10)
        item("item.dark_bow")
        item("item.hunters_crossbow")
        item("item.long_kebbit_bolts", 10)

        item("item.zanik_s_crossbow")
        item("item.karil_s_crossbow")
        item("item.bolt_rack", 10)

        item("item.morrigan_s_throwing_axe", 10)
        item("item.morrigan_s_javelin", 10)
        item("item.dragonfire_ward_uncharged", 10)
        item("item.crystal_helm", 10)
        item("item.crystal_body", 10)
        item("item.crystal_legs", 10)
        item("item.crystal_bow_full", 10)
        item("item.bow_of_faerdhinen", 10)
        item("item.zaryte_crossbow", 10)
        item("item.zaryte_bow", 10)
        item("item.craw_s_bow", 10)
    }
}
