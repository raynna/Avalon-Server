package com.rs.kotlin.game.world.activity.pvpgame.tournament

import com.rs.java.game.item.Item
import com.rs.java.game.player.content.presets.Preset

object TournamentPresetDefaults {

    /* -------------------------------- */
    /* 60 ATTACK PURE (NH)     */
    /* -------------------------------- */


    fun pureNHHybridWelfare(): TournamentPreset {
        val equipment = arrayOf(
            Item("item.ghostly_hood"),
            Item("item.mithril_gloves"),
            Item("item.saradomin_cape"),
            Item("item.amulet_of_glory"),
            Item("item.zamorak_s_unholy_book"),
            Item("item.ancient_staff"),
            Item("item.ring_of_recoil"),
            Item("item.rock_climbing_boots"),
            Item("item.ghostly_robe"),
            Item("item.ghostly_robe_2"),
            Item("item.dragonstone_bolts_e", 1000),
        )

        val inventory = arrayOf(
            Item("item.rune_crossbow"),
            Item("item.ava_s_accumulator"),
            Item("item.super_combat_potion_4"),
            Item("item.ranging_potion_4"),
            Item("item.black_d_hide_chaps"),
            Item("item.dragon_dagger_p++"),
            Item("item.super_restore_4", 2),
            Item("item.rocktail", 2),
            Item("item.saradomin_brew_4", 2),
            Item("item.rocktail", 15),
            Item("item.blood_rune", 200),
            Item("item.death_rune", 400),
            Item("item.water_rune", 600),

        )
        val levels = intArrayOf(
            60,  // Attack
            1,  // Defence
            99,  // Strength
            99,  // Hitpoints
            99,  // Ranged
            52,  // Prayer
            99   // Magic
        )
        val preset = Preset(
            "[NH] 60 Attack welfare pure",
            inventory,
            equipment,
            false,
            1,
            levels,
            emptyArray(),
            null
        )

        val rules = TournamentRules()
        return TournamentPreset(preset, rules)
    }
    /* -------------------------------- */
    /* 60 ATTACK ZERKER (VENGEANCE)     */
    /* -------------------------------- */
    fun attack60ZerkerVengeance(): TournamentPreset {

        val equipment = arrayOf(
            Item("item.berserker_helm"),
            Item("item.barrows_gloves"),
            Item("item.tokhaar_kal"),
            Item("item.amulet_of_fury"),
            Item("item.rune_defender"),
            Item("item.dragon_scimitar"),
            Item("item.berserker_ring_i"),
            Item("item.rune_boots"),
            Item("item.rune_platebody"),
            Item("item.rune_plateskirt")
        )

        val inventory = arrayOf(
            Item("item.dragon_dagger_p++"),
            Item("item.super_combat_potion_4", 2),
            Item("item.super_restore_4"),
            Item("item.rocktail"),
            Item("item.saradomin_brew_4", 2),
            Item("item.super_restore_4"),
            Item("item.rocktail", 17),
            Item("item.death_rune", 40),
            Item("item.astral_rune", 80),
            Item("item.earth_rune", 200)
        )

        val levels = intArrayOf(
            60,  // Attack
            45,  // Defence
            99,  // Strength
            99,  // Hitpoints
            99,  // Ranged
            95,  // Prayer
            99   // Magic
        )

        val preset = Preset(
            "[Vengeance] 60 Attack Zerker",
            inventory,
            equipment,
            true,
            2,
            levels,
            emptyArray(),
            null
        )

        val rules = TournamentRules(
            protectionPrayersAllowed = false
        )

        return TournamentPreset(preset, rules)
    }

    /* -------------------------------- */
    /* 60 ATTACK ZERKER HYBRID          */
    /* -------------------------------- */

    fun attack60ZerkerHybrid(): TournamentPreset {

        val equipment = arrayOf(
            Item("item.berserker_helm"),
            Item("item.barrows_gloves"),
            Item("item.saradomin_cape"),
            Item("item.arcane_stream_necklace"),
            Item("item.spirit_shield"),
            Item("item.ancient_staff"),
            Item("item.berserker_ring_i"),
            Item("item.rock_climbing_boots"),
            Item("item.mystic_robe_top"),
            Item("item.mystic_robe_bottom")
        )

        val inventory = arrayOf(
            Item("item.rune_platebody"),
            Item("item.dragon_scimitar"),
            Item("item.amulet_of_fury"),
            Item("item.saradomin_body"),
            Item("item.rune_plateskirt"),
            Item("item.rune_defender"),
            Item("item.fire_cape"),
            Item("item.saradomin_chaps"),
            Item("item.rocktail"),
            Item("item.dragon_dagger_p++"),
            Item("item.super_combat_potion_4"),
            Item("item.super_restore_4"),
            Item("item.rocktail"),
            Item("item.saradomin_brew_4", 2),
            Item("item.super_restore_4"),
            Item("item.rocktail", 9),
            Item("item.blood_rune", 200),
            Item("item.death_rune", 400),
            Item("item.water_rune", 600)
        )

        val levels = intArrayOf(
            60, 45, 99, 99, 99, 95, 99
        )

        val preset = Preset(
            "[Hybrid] 60 Attack Zerker",
            inventory,
            equipment,
            true,
            1,
            levels,
            emptyArray(),
            null
        )

        val rules = TournamentRules(
            protectionPrayersAllowed = false
        )

        return TournamentPreset(preset, rules)
    }

    /* -------------------------------- */
    /* MAX STRENGTH MAIN                */
    /* -------------------------------- */

    fun maxStrength(): TournamentPreset {

        val equipment = arrayOf(
            Item("item.neitiznot_faceguard"),
            Item("item.goliath_gloves_red"),
            Item("item.tokhaar_kal"),
            Item("item.amulet_of_rancour"),
            Item("item.avernic_defender"),
            Item("item.abyssal_vine_whip"),
            Item("item.berserker_ring_i"),
            Item("item.steadfast_boots"),
            Item("item.vesta_s_chainbody"),
            Item("item.vesta_s_plateskirt")
        )

        val inventory = arrayOf(
            Item("item.armadyl_godsword"),
            Item("item.super_combat_potion_4", 2),
            Item("item.super_restore_4"),
            Item("item.rocktail"),
            Item("item.saradomin_brew_4", 2),
            Item("item.super_restore_4"),
            Item("item.rocktail", 17),
            Item("item.death_rune", 40),
            Item("item.astral_rune", 80),
            Item("item.earth_rune", 200)
        )

        val levels = intArrayOf(
            99, 99, 99, 99, 99, 99, 99
        )

        val preset = Preset(
            "maxstr",
            inventory,
            equipment,
            true,
            2,
            levels,
            emptyArray(),
            null
        )

        val rules = TournamentRules(
            protectionPrayersAllowed = false
        )

        return TournamentPreset(preset, rules)
    }

    /* -------------------------------- */
    /* POOL + RANDOM                    */
    /* -------------------------------- */

    val PRESETS = listOf(
        pureNHHybridWelfare(),
        attack60ZerkerVengeance(),
        attack60ZerkerHybrid(),
        maxStrength()
    )

    fun random(): TournamentPreset = PRESETS.random()
}
