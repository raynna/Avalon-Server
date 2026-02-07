package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object SuppliesShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Supplies Store"
        currency = CurrencyType.COINS
        isGlobal = true

        item("item.super_strength_4")
        item("item.super_attack_4")
        item("item.super_defence_4")
        item("item.ranging_potion_4")
        item("item.magic_potion_4")
        item("item.super_combat_potion_4")
        item("item.strength_potion_4")

        item("item.saradomin_brew_4")
        item("item.super_restore_4")
        item("item.sanfew_serum_4")
        item("item.prayer_renewal_4")
        item("item.super_antifire_4")
        item("item.overload_4")
        item("item.super_prayer_4")

        item("item.trout")
        item("item.lobster")
        item("item.swordfish")
        item("item.monkfish")
        item("item.shark")
        item("item.cavefish")
        item("item.rocktail")

        item("item.tuna_potato")
        item("item.cooked_karambwan")
        item("item.pineapple_pizza")
        item(10476)
        item("item.teleport_to_house")
        item("item.lumber_yard_teleport")
        item(8015)
        item("item.potion_flask")
    }
}
