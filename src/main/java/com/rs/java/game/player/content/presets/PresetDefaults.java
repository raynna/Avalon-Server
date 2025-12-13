package com.rs.java.game.player.content.presets;

import com.rs.java.game.item.Item;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.summoning.Summoning;

import java.util.Arrays;

public class PresetDefaults {

    public static Preset zerkerPreset() {
        Item[] inventory = {
            new Item(385, 20), // Shark
            new Item(6685, 4), // Saradomin brew
            new Item(3024, 4), // Super restore
            new Item(560, 500), // Death rune
            new Item(565, 500), // Blood rune
            new Item(555, 1000), // Water rune
            new Item(2441, 1), // Super attack
            new Item(2443, 1), // Super defence
            new Item(2437, 1), // Super strength
        };
        Item[] equipment = {
            new Item("item.berserker_helm"),
            new Item("item.amulet_of_fury"),
            new Item("item.dragon_scimitar"),
            new Item("item.rune_platebody"),
            new Item("item.rune_platelegs"),
            new Item("item.climbing_boots"),
            new Item("item.barrows_gloves"),
            new Item("item.rune_defender"),
        };
        return new Preset("zerker", inventory, equipment, false, (byte) 0,
                new double[7], new Item[0], null);
    }

    public static Preset dharokPreset() {
        Item[] equipment = {
            new Item(4716), // Dharok helm
            new Item(4718), // Dharok greataxe
            new Item(4720), // Dharok platebody
            new Item(4722), // Dharok platelegs
            new Item(3105), // Climbing boots
            new Item(8850), // Rune defender
            new Item(6585), // Fury
            new Item(7462)  // Barrows gloves
        };
        Item[] inventory = {
            new Item(385, 20), // Sharks
            new Item(6685, 4), // Saradomin brew
            new Item(3024, 4), // Super restore
            new Item(2437, 1), // Super strength
            new Item(2441, 1), // Super attack
        };
        return new Preset("dharok", inventory, equipment, false, (byte) 0,
                new double[7], new Item[0], null);
    }

    public static Preset hybridPreset() {
        Item[] equipment = {
            new Item(10828), // Neitiznot
            new Item(6585), // Fury
            new Item(4151), // Abyssal whip
            new Item(4712), // Ahrim top
            new Item(4714), // Ahrim bottom
            new Item(7462), // Barrows gloves
            new Item(3105), // Climbing boots
            new Item(8850), // Rune defender
        };
        Item[] inventory = {
            new Item(560, 500), // Death runes
            new Item(565, 500), // Blood runes
            new Item(555, 1000), // Water runes
            new Item(3024, 6), // Restores
            new Item(6685, 4), // Brews
            new Item(385, 20) // Sharks
        };
        return new Preset("hybrid", inventory, equipment, false, (byte) 2,
                new double[7], new Item[]{
                new Item(560, 500), new Item(565, 500), new Item(555, 1000)}, null);
    }

    public static Preset maxStrengthPreset() {
        Item[] equipment = {
            new Item(10828), // Neitiznot
            new Item(11732), // Dragon boots
            new Item(7462), // Barrows gloves
            new Item(6585), // Fury
            new Item(4151), // Abyssal whip
            new Item(8850), // Rune defender
            new Item(1127), // Rune platebody
            new Item(1079), // Rune platelegs
        };
        Item[] inventory = {
            new Item(385, 20),
            new Item(2437, 1),
            new Item(2441, 1),
            new Item(2443, 1)
        };
        return new Preset("maxstr", inventory, equipment, false, (byte) 0,
                new double[7], new Item[0], null);
    }
}
