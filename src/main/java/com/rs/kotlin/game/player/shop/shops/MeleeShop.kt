package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object MeleeShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Melee Equipment Store"
        currency = CurrencyType.COINS
        isGlobal = true

        item("item.proselyte_sallet")
        item("item.proselyte_hauberk")
        item("item.proselyte_cuisse")
        item("item.proselyte_tasset")
        item("item.berserker_helm")
        item("item.warrior_helm")
        item("item.helm_of_neitiznot")

        item("item.rune_full_helm")
        item("item.rune_platebody")
        item("item.rune_platelegs")
        item("item.rune_plateskirt")
        item("item.rune_kiteshield")
        item("item.rune_boots")
        item("item.toktz_ket_xil")

        item("item.dragon_helm")
        item("item.dragon_chainbody")
        item("item.dragon_platelegs")
        item("item.dragon_plateskirt")
        item("item.dragon_sq_shield")
        item("item.dragon_boots")
        item("item.obsidian_cape")

        item("item.rock_shell_helm")
        item("item.rock_shell_plate")
        item("item.rock_shell_legs")
        item("item.granite_helm")
        item("item.granite_body")
        item("item.granite_legs")
        item("item.granite_shield")

        item("item.bandos_chestplate")
        item("item.bandos_tassets")
        item("item.bandos_boots")
        item("item.steadfast_boots")
        item("item.iron_scimitar")
        item("item.rune_scimitar")
        item("item.brine_sabre")
        item("item.brackish_blade")

        item("item.abyssal_whip")
        item("item.granite_maul")
        item("item.dragon_scimitar")
        item("item.dragon_dagger_p++")
        item("item.dragon_mace")
        item("item.dragon_battleaxe")
        item("item.dragon_2h_sword")
        item("item.dragon_halberd")

        item("item.dragon_spear")
        item("item.dragon_longsword")
        item("item.toktz_xil_ak")
        item("item.tzhaar_ket_em")
        item("item.toktz_xil_ek")
        item("item.tzhaar_ket_om")
        item("item.granite_mace")
    }
}
