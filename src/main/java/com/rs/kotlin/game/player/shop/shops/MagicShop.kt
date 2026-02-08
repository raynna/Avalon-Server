package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object MagicShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Magic Equipment Store"
        currency = CurrencyType.COINS
        isGlobal = true
        item("item.air_rune")
        item("item.water_rune")
        item("item.earth_rune")
        item("item.fire_rune")
        item("item.chaos_rune")
        item("item.death_rune")
        item("item.blood_rune")
        item("item.nature_rune")
        item("item.law_rune")
        item("item.cosmic_rune")
        item("item.astral_rune")
        item("item.soul_rune")
        item("item.lava_rune")
        item("item.mud_rune")
        item("item.mist_rune")
        item("item.dust_rune")
        item("item.smoke_rune")
        item("item.armadyl_rune")

        item("item.water_battlestaff")
        item("item.ancient_staff")
        item("item.staff_of_light")
        item("item.ragefire_boots")
        item("item.ahrim_s_staff")
        item("item.mystic_mud_staff")
        item("item.master_wand")
        item("item.saradomin_staff")
        item("item.zamorak_staff")
        item("item.guthix_staff")

        item("item.mages_book")
        item("item.infinity_hat")
        item("item.infinity_top")
        item("item.infinity_bottoms")
        item("item.saradomin_cape")
        item("item.zamorak_cape")
        item("item.guthix_cape")
        item("item.infinity_boots")
        item("item.dagon_hai_hat")
        item("item.ghostly_hood")
        item("item.wizard_hat")
        item("item.enchanted_hat")
        item("item.mystic_hat")
        item("item.ahrim_s_hood")

        item("item.mystic_boots")
        item("item.dagon_hai_robe_top")
        item("item.ghostly_robe")
        item("item.wizard_robe_top")
        item("item.enchanted_top")
        item("item.mystic_robe_top")
        item("item.ahrim_s_robe_top")

        item("item.wizard_boots")
        item("item.dagon_hai_robe_bottom")
        item("item.ghostly_robe_2")
        item("item.wizard_robe_skirt")
        item("item.enchanted_robe")
        item("item.mystic_robe_bottom")
        item("item.ahrim_s_robe_skirt")

        item("item.zamorak_robe")
        item("item.zamorak_robe_2")

        item("item.spirit_shield")
        item("item.blessed_spirit_shield")

        item("item.fungal_visor")
        item("item.fungal_poncho")
        item("item.fungal_leggings")
        item("item.grifolic_visor")
        item("item.grifolic_poncho")
        item("item.grifolic_leggings")
        item("item.polypore_staff")
        item("item.greater_runic_staff_inactive")
        item("item.zuriel_s_staff")
        item("item.ancient_wyvern_shield_uncharged")
        item("item.thammaron_s_sceptre")
        item("item.staff_of_the_dead")
    }
}
