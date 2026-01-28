package com.rs.java.game.player.content.presets;

import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.summoning.Summoning;

import java.util.Arrays;

public class PresetDefaults {

    public static Preset attack60ZerkerVengeance() {
        Item[] equipment = {
                new Item("item.berserker_helm"),
                new Item("item.barrows_gloves"),
                new Item("item.tokhaar_kal"),
                new Item("item.amulet_of_fury"),
                new Item("item.rune_defender"),
                new Item("item.dragon_scimitar"),
                new Item("item.berserker_ring_i"),
                new Item("item.rune_boots"),
                new Item("item.rune_platebody"),
                new Item("item.rune_plateskirt"),
        };
        Item[] inventory = {
                new Item("item.dragon_dagger_p++", 1),
                new Item("item.super_combat_potion_4", 2),
                new Item("item.super_restore_4", 1),
                new Item("item.rocktail", 1),
                new Item("item.saradomin_brew_4", 2),
                new Item("item.super_restore_4", 1),
                new Item("item.rocktail", 17),
                new Item("item.death_rune", 40),
                new Item("item.astral_rune", 80),
                new Item("item.earth_rune", 200),
        };

        double[] levels = {
                60,  // Attack
                45,  // Defence
                99,  // Strength
                99,  // Hitpoints
                99,   // Ranged
                95,  // Prayer
                99   // Magic
        };
        return new Preset("[Vengeance] 60 Attack Zerker", inventory, equipment, true, (byte) 2,
                levels, new Item[0], null);
    }

    public static Preset attack60ZerkerHybrid() {
        Item[] equipment = {
                new Item("item.berserker_helm"),
                new Item("item.barrows_gloves"),
                new Item("item.saradomin_cape"),
                new Item("item.arcane_stream_necklace"),
                new Item("item.spirit_shield"),
                new Item("item.ancient_staff"),
                new Item("item.berserker_ring_i"),
                new Item("item.rock_climbing_boots"),
                new Item("item.mystic_robe_top"),
                new Item("item.mystic_robe_bottom"),
        };
        Item[] inventory = {
                new Item("item.rune_platebody", 1),
                new Item("item.dragon_scimitar", 1),
                new Item("item.amulet_of_fury", 1),
                new Item("item.saradomin_body", 1),
                new Item("item.rune_plateskirt", 1),
                new Item("item.rune_defender", 1),
                new Item("item.fire_cape", 1),
                new Item("item.saradomin_chaps", 1),
                new Item("item.rocktail", 1),
                new Item("item.dragon_dagger_p++", 1),
                new Item("item.super_combat_potion_4", 1),
                new Item("item.super_restore_4", 1),
                new Item("item.rocktail", 1),
                new Item("item.saradomin_brew_4", 2),
                new Item("item.super_restore_4", 1),
                new Item("item.rocktail", 9),
                new Item("item.blood_rune", 200),
                new Item("item.death_rune", 400),
                new Item("item.water_rune", 600),
        };

        double[] levels = {
                60,  // Attack
                45,  // Defence
                99,  // Strength
                99,  // Hitpoints
                99,   // Ranged
                95,  // Prayer
                99   // Magic
        };
        return new Preset("[Hybrid] 60 Attack Zerker", inventory, equipment, true, (byte) 1,
                levels, new Item[0], null);
    }

    public static Preset maxStrengthPreset() {
        Item[] equipment = {
            new Item("item.neitiznot_faceguard"),
            new Item("item.goliath_gloves_red"),
            new Item("item.tokhaar_kal"),
            new Item("item.amulet_of_rancour"),
            new Item("item.avernic_defender"),
            new Item("item.abyssal_vine_whip"),
            new Item("item.berserker_ring_i"),
            new Item("item.steadfast_boots"),
            new Item("item.vesta_s_chainbody"),
            new Item("item.vesta_s_plateskirt"),
        };
        Item[] inventory = {
            new Item("item.armadyl_godsword", 1),
            new Item("item.super_combat_potion_4", 2),
            new Item("item.super_restore_4", 1),
            new Item("item.rocktail", 1),
            new Item("item.saradomin_brew_4", 2),
            new Item("item.super_restore_4", 1),
            new Item("item.rocktail", 17),
            new Item("item.death_rune", 40),
            new Item("item.astral_rune", 80),
            new Item("item.earth_rune", 200),
        };
        double[] levels = {
                99,  // Attack
                99,  // Defence
                99,  // Strength
                99,  // Hitpoints
                99,   // Ranged
                99,  // Prayer
                99   // Magic
        };
        return new Preset("maxstr", inventory, equipment, true, (byte) 2,
                levels, new Item[0], null);
    }

    public static final Preset[] TOURNAMENT_PRESETS = {
            attack60ZerkerVengeance(),
            attack60ZerkerHybrid(),
            maxStrengthPreset()
    };

    public static Preset randomTournamentPreset() {
        return TOURNAMENT_PRESETS[
                (int) (Math.random() * TOURNAMENT_PRESETS.length)
                ];
    }
}
