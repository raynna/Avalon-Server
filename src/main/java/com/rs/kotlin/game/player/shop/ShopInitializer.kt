package com.rs.kotlin.game.player.shop

import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object ShopInitializer {
    @JvmStatic
    fun initializeShops() {
        // --------------------
        // Supplies Store
        // --------------------
        GlobalShopManager.registerShop(
            shop {
                id = 1
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
                item(10476) // Purple sweet
                item("item.teleport_to_house")
                item("item.lumber_yard_teleport")
                item(8015)  // Bones to peaches
                item("item.wolpertinger_pouch")
                item("item.magic_focus_scroll")
                item("item.steel_titan_pouch")
                item("item.steel_of_legends_scroll")

                item("item.potion_flask")
            }
        )

        GlobalShopManager.registerShop(
            shop {
                id = 2
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
        )


        // --------------------
        // Ranged Equipment Store
        // --------------------
        GlobalShopManager.registerShop(
            shop {
                id = 3
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
                item("item.ava_s_accumulator")
                item("item.ava_s_alerter")
                item("item.iron_knife", 10)
                item("item.rune_knife", 10)
                item("item.dragon_dart", 10)
                item("item.toktz_xil_ul", 10)

                item("item.shortbow")
                item("item.yew_shortbow")
                item("item.magic_shortbow")
                item("item.rune_crossbow")
                item("item.iron_arrow", 10)
                item("item.adamant_arrow", 10)
                item("item.rune_arrow", 10)

                item("item.dragon_arrow", 10)
                item("item.ruby_bolts_e", 10)
                item("item.diamond_bolts_e", 10)
                item("item.dragon_bolts_e", 10)
                item("item.dark_bow")
                item("item.hunters_crossbow")
                item("item.long_kebbit_bolts", 10)

                item("item.zanik_s_crossbow")
                item("item.karil_s_crossbow")
                item("item.bolt_rack", 10)

                item("item.morrigan_s_throwing_axe", 10)
                item("item.morrigan_s_javelin", 10)
            }
        )

        // --------------------
        // Magic Equipment Store
        // --------------------
        GlobalShopManager.registerShop(
            shop {
                id = 4
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
            }
        )

        // --------------------
        // Accessories Store
        // --------------------
        GlobalShopManager.registerShop(
            shop {
                id = 5
                title = "Accessories Store"
                currency = CurrencyType.COINS
                isGlobal = true

                item("item.ring_of_recoil")
                item("item.ring_of_duelling_8")
                item("item.ring_of_life")
                item("item.amulet_of_power")
                item("item.amulet_of_strength")
                item("item.amulet_of_glory")
                item("item.amulet_of_fury")
                item("item.berserker_necklace")
                item("item.combat_bracelet")
                item("item.regen_bracelet")
                item("item.berserker_ring")
                item("item.warrior_ring")
                item("item.archers_ring")
                item("item.seers_ring")
                item("item.culinaromancer_s_gloves_6")
                item("item.culinaromancer_s_gloves_7")
                item("item.culinaromancer_s_gloves_8")
                item("item.culinaromancer_s_gloves_9")
                item("item.culinaromancer_s_gloves_10")
                item("item.fighter_torso")
                item("item.fighter_hat")
                item("item.mithril_defender")
                item("item.adamant_defender")
                item("item.rune_defender")
                item("item.dragon_defender")
                item("item.fire_cape")  // Fire cape
                item("item.tokhaar_kal") // TokHaar-Kal
            }
        )


        // --------------------
        // Barrows Store
        // --------------------
        GlobalShopManager.registerShop(
            shop {
                id = 6
                title = "Barrows Store"
                currency = CurrencyType.COINS
                isGlobal = true

                for (i in 4708..4738 step 2) {
                    item(i, 1)
                }
                for (i in 4745..4759 step 2) {
                    item(i, 1)
                }
                for (i in 21736..21760 step 8) {
                    item(i, 1)
                }
            }
        )

        // --------------------
        // Pvp token store
        // --------------------
        GlobalShopManager.registerShop(
            shop {
                id = 10
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
            }
        )
    }
}
