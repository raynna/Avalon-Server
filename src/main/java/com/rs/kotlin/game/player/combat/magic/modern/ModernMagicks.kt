package com.rs.kotlin.game.player.combat.magic.modern

import com.rs.java.game.Graphics
import com.rs.java.game.WorldTile
import com.rs.kotlin.game.player.combat.magic.ElementType
import com.rs.kotlin.game.player.combat.magic.ItemRequirement
import com.rs.kotlin.game.player.combat.magic.RuneDefinitions
import com.rs.kotlin.game.player.combat.magic.RuneRequirement
import com.rs.kotlin.game.player.combat.magic.Spell
import com.rs.kotlin.game.player.combat.magic.SpellType
import com.rs.kotlin.game.player.combat.magic.Spellbook
import com.rs.kotlin.game.player.combat.magic.StaffRequirement
import com.rs.kotlin.game.world.projectile.Projectile
import com.rs.kotlin.rscm.Rscm

object ModernMagicks : Spellbook(MODERN_ID) {
    override val spells =
        listOf(
            Spell(
                id = 24,
                name = "Home Teleport",
                level = 0,
                xp = 0.0,
                type = SpellType.Teleport,
                runes = emptyList(),
                teleportLocation = WorldTile(3093, 3495, 0),
            ),
            Spell(
                id = 37,
                name = "Mobilising Armies Teleport",
                level = 10,
                xp = 19.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                    ),
                teleportLocation = WorldTile(2413, 2848, 0),
            ),
            Spell(
                id = 40,
                name = "Varrock Teleport",
                level = 25,
                xp = 35.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 1),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                    ),
                teleportLocation = WorldTile(3212, 3424, 0),
            ),
            Spell(
                id = 43,
                name = "Lumbridge Teleport",
                level = 31,
                xp = 41.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 1),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                    ),
                teleportLocation = WorldTile(3222, 3218, 0),
            ),
            Spell(
                id = 46,
                name = "Falador Teleport",
                level = 37,
                xp = 48.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                    ),
                teleportLocation = WorldTile(2964, 3379, 0),
            ),
            Spell(
                id = 48,
                name = "House Teleport",
                level = 40,
                xp = 25.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 1),
                    ),
                teleportLocation = WorldTile(3222, 3222, 0),
            ),
            Spell(
                id = 51,
                name = "Camelot Teleport",
                level = 45,
                xp = 55.5,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                    ),
                teleportLocation = WorldTile(2757, 3478, 0),
            ),
            Spell(
                id = 57,
                name = "Ardougne Teleport",
                level = 51,
                xp = 61.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                    ),
                teleportLocation = WorldTile(2664, 3305, 0),
            ),
            Spell(
                id = 62,
                name = "Watchtower Teleport",
                level = 58,
                xp = 68.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                    ),
                teleportLocation = WorldTile(2547, 3113, 2),
            ),
            Spell(
                id = 69,
                name = "Trollheim Teleport",
                level = 61,
                xp = 68.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 2),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                    ),
                teleportLocation = WorldTile(2888, 3674, 0),
            ),
            Spell(
                id = 72,
                name = "Ape Atoll Teleport",
                level = 64,
                xp = 76.0,
                type = SpellType.Teleport,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                    ),
                teleportLocation = WorldTile(2762, 9094, 0),
            ),
            Spell(
                id = 98,
                name = "Wind Rush",
                level = 1,
                xp = 2.5,
                damage = 10,
                attackSound = 220,
                hitSound = 221,
                type = SpellType.Combat,
                element = ElementType.Air,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2, true),
                    ),
                animationId = 14221,
                graphicId = Graphics(457),
                projectileId = 458,
                endGraphic = Graphics(463, 100),
            ),
            Spell(
                id = 25,
                name = "Wind Strike",
                level = 1,
                xp = 5.5,
                damage = 20,
                attackSound = 220,
                hitSound = 221,
                type = SpellType.Combat,
                element = ElementType.Air,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1, true),
                        RuneRequirement(RuneDefinitions.Runes.MIND, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(457),
                projectileId = 458,
                endGraphic = Graphics(463, 100),
            ),
            Spell(
                id = 28,
                name = "Water Strike",
                level = 5,
                xp = 7.5,
                damage = 40,
                attackSound = 211,
                hitSound = 212,
                type = SpellType.Combat,
                element = ElementType.Water,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                        RuneRequirement(RuneDefinitions.Runes.MIND, 1),
                    ),
                animationId = 14220,
                graphicId = Graphics(2701),
                projectileId = 2703,
                endGraphic = Graphics(2708, 100),
            ),
            Spell(
                id = 30,
                name = "Earth Strike",
                level = 9,
                xp = 9.5,
                damage = 60,
                attackSound = 132,
                hitSound = 133,
                type = SpellType.Combat,
                element = ElementType.Earth,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                        RuneRequirement(RuneDefinitions.Runes.MIND, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(2713),
                projectileId = 2718,
                endGraphic = Graphics(2723, 100),
            ),
            Spell(
                id = 32,
                name = "Fire Strike",
                level = 13,
                xp = 11.5,
                damage = 80,
                attackSound = 160,
                hitSound = 161,
                type = SpellType.Combat,
                element = ElementType.Fire,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                        RuneRequirement(RuneDefinitions.Runes.MIND, 1),
                    ),
                animationId = 2791,
                graphicId = Graphics(2728),
                projectileId = 2729,
                endGraphic = Graphics(2737, 100),
            ),
            Spell(
                id = 34,
                name = "Wind Bolt",
                level = 17,
                xp = 13.5,
                damage = 90,
                attackSound = 218,
                hitSound = 219,
                type = SpellType.Combat,
                element = ElementType.Air,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(457),
                projectileId = 459,
                endGraphic = Graphics(464, 100),
            ),
            Spell(
                id = 39,
                name = "Water Bolt",
                level = 23,
                xp = 16.5,
                damage = 100,
                attackSound = 209,
                hitSound = 210,
                type = SpellType.Combat,
                element = ElementType.Water,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 1),
                    ),
                animationId = 14220,
                graphicId = Graphics(2701),
                projectileId = 2704,
                endGraphic = Graphics(2709, 100),
            ),
            Spell(
                id = 42,
                name = "Earth Bolt",
                level = 29,
                xp = 19.5,
                damage = 110,
                attackSound = 130,
                hitSound = 131,
                type = SpellType.Combat,
                element = ElementType.Earth,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(2714),
                projectileId = 2719,
                endGraphic = Graphics(2724, 100),
            ),
            Spell(
                id = 45,
                name = "Fire Bolt",
                level = 35,
                xp = 22.5,
                damage = 120,
                attackSound = 157,
                hitSound = 158,
                type = SpellType.Combat,
                element = ElementType.Fire,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 4),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 1),
                    ),
                animationId = 2791,
                graphicId = Graphics(2728),
                projectileId = 2731,
                endGraphic = Graphics(2738, 100),
            ),
            Spell(
                id = 47,
                name = "Crumble Undead",
                level = 39,
                xp = 24.5,
                damage = 160,
                type = SpellType.Combat,
                attackSound = 122,
                hitSound = 124,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 1),
                    ),
                animationId = 724,
                graphicId = Graphics(145, 100),
                projectileId = 146,
                projectileType = Projectile.CRUMBLE_UNDEAD,
                endGraphic = Graphics(147, 100),
            ),
            Spell(
                id = 49,
                name = "Wind Blast",
                level = 41,
                xp = 25.5,
                damage = 130,
                attackSound = 216,
                hitSound = 217,
                type = SpellType.Combat,
                element = ElementType.Air,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(457),
                projectileId = 460,
                endGraphic = Graphics(464, 100),
            ),
            Spell(
                id = 52,
                name = "Water Blast",
                level = 47,
                xp = 28.5,
                damage = 140,
                attackSound = 207,
                hitSound = 208,
                type = SpellType.Combat,
                element = ElementType.Water,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 14220,
                graphicId = Graphics(2701),
                projectileId = 2705,
                endGraphic = Graphics(2710, 100),
            ),
            Spell(
                id = 54,
                name = "Iban Blast",
                level = 50,
                xp = 30.0,
                damage = 250,
                attackSound = 162,
                hitSound = 1341,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                staff = StaffRequirement(listOf(1409)),
                animationId = 708,
                graphicId = Graphics(87, 100),
                endGraphic = Graphics(89, 50),
                projectileType = Projectile.IBAN_BLAST,
                projectileId = 88,
            ),
            Spell(
                id = 56,
                name = "Magic Dart",
                level = 50,
                xp = 30.0,
                damage = 0, // special calc for this
                attackSound = 1718,
                hitSound = 174,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                        RuneRequirement(RuneDefinitions.Runes.MIND, 4),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.slayer_s_staff"),
                                Rscm.item("item.staff_of_light"),
                                Rscm.item("item.staff_of_light_blue"),
                                Rscm.item("item.staff_of_light_gold"),
                                Rscm.item("item.staff_of_light_green"),
                                Rscm.item("item.staff_of_light_red"),
                            ),
                    ),
                animationId = 1575,
                projectileType = Projectile.SLAYER_DART,
                projectileId = 328,
                endGraphic = Graphics(329, 100),
            ),
            Spell(
                id = 58,
                name = "Earth Blast",
                level = 53,
                xp = 31.5,
                damage = 150,
                attackSound = 128,
                hitSound = 129,
                type = SpellType.Combat,
                element = ElementType.Earth,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 4),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(2715),
                projectileId = 2720,
                endGraphic = Graphics(2725, 100),
            ),
            Spell(
                id = 63,
                name = "Fire Blast",
                level = 59,
                xp = 34.5,
                damage = 160,
                attackSound = 155,
                hitSound = 156,
                type = SpellType.Combat,
                element = ElementType.Fire,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 4),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 2791,
                graphicId = Graphics(2728),
                projectileId = 2733,
                endGraphic = Graphics(2739, 100),
            ),
            Spell(
                id = 66,
                name = "Saradomin Strike",
                level = 60,
                xp = 35.0,
                damage = 200,
                attackSound = 1659,
                chargeBoost = true,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 2),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 4),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.saradomin_staff"),
                            ),
                    ),
                itemRequirement =
                    ItemRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.saradomin_cape"),
                                Rscm.item("item.imbued_saradomin_cape"),
                            ),
                    ),
                animationId = 811,
                endGraphic = Graphics(76, 100),
            ),
            Spell(
                id = 67,
                name = "Claws of Guthix",
                level = 60,
                xp = 35.0,
                damage = 200,
                chargeBoost = true,
                attackSound = 1653,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 1),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 4),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.guthix_staff"),
                                Rscm.item("item.void_knight_mace"),
                            ),
                    ),
                itemRequirement =
                    ItemRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.guthix_cape"),
                                Rscm.item("item.imbued_guthix_cape"),
                            ),
                    ),
                animationId = 811,
                endGraphic = Graphics(77, 100),
            ),
            Spell(
                id = 68,
                name = "Flames of Zamorak",
                level = 60,
                xp = 35.0,
                damage = 200,
                chargeBoost = true,
                attackSound = 1655,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 4),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                    ),
                staff =
                    StaffRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.zamorak_staff"),
                            ),
                    ),
                itemRequirement =
                    ItemRequirement(
                        anyOf =
                            listOf(
                                Rscm.item("item.zamorak_cape"),
                                Rscm.item("item.imbued_zamorak_cape"),
                            ),
                    ),
                animationId = 811,
                endGraphic = Graphics(78, 0),
            ),
            Spell(
                id = 70,
                name = "Wind Wave",
                level = 62,
                xp = 36.0,
                damage = 170,
                attackSound = 222,
                hitSound = 223,
                type = SpellType.Combat,
                element = ElementType.Air,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(457),
                projectileId = 461,
                endGraphic = Graphics(2699, 100),
            ),
            Spell(
                id = 73,
                name = "Water Wave",
                level = 65,
                xp = 37.5,
                damage = 180,
                attackSound = 213,
                hitSound = 214,
                type = SpellType.Combat,
                element = ElementType.Water,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 7),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(2702),
                projectileId = 2706,
                endGraphic = Graphics(2710, 100),
            ),
            Spell(
                id = 77,
                name = "Earth Wave",
                level = 70,
                xp = 40.0,
                damage = 190,
                attackSound = 134,
                hitSound = 135,
                type = SpellType.Combat,
                element = ElementType.Earth,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 7),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                    ),
                animationId = 14221,
                graphicId = Graphics(2716),
                projectileId = 2721,
                endGraphic = Graphics(2726, 100),
            ),
            Spell(
                id = 80,
                name = "Fire Wave",
                level = 75,
                xp = 42.5,
                damage = 200,
                attackSound = 162,
                hitSound = 163,
                type = SpellType.Combat,
                element = ElementType.Fire,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 7),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                    ),
                animationId = 2791,
                graphicId = Graphics(2728, 0),
                projectileId = 2735,
                endGraphic = Graphics(2740, 100),
            ),
            Spell(
                id = 84,
                name = "Wind Surge",
                level = 81,
                xp = 44.5,
                damage = 220,
                attackSound = 7866,
                hitSound = 7867,
                type = SpellType.Combat,
                element = ElementType.Air,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 7),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 10546,
                graphicId = Graphics(457),
                projectileId = 462,
                endGraphic = Graphics(2700, 100),
            ),
            Spell(
                id = 87,
                name = "Water Surge",
                level = 85,
                xp = 46.5,
                damage = 240,
                attackSound = 7834,
                hitSound = 7822,
                type = SpellType.Combat,
                element = ElementType.Water,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 10),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 7),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 10542,
                graphicId = Graphics(2701),
                projectileId = 2707,
                endGraphic = Graphics(2712, 100),
            ),
            Spell(
                id = 89,
                name = "Earth Surge",
                level = 90,
                xp = 48.5,
                damage = 260,
                attackSound = 7914,
                hitSound = 7919,
                type = SpellType.Combat,
                element = ElementType.Earth,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 10),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 7),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 14209,
                graphicId = Graphics(2717),
                projectileId = 2722,
                endGraphic = Graphics(2727, 100),
            ),
            Spell(
                id = 91,
                name = "Fire Surge",
                level = 95,
                xp = 50.5,
                damage = 280,
                attackSound = 7932,
                hitSound = 7933,
                type = SpellType.Combat,
                element = ElementType.Fire,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 10),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 10),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                projectileIds = listOf(2736, 2735, 2736),
                animationId = 2791,
                graphicId = Graphics(2728),
                endGraphic = Graphics(2741, 100),
            ),
            Spell(
                id = 99,
                name = "Storm of Armadyl",
                level = 77,
                xp = 70.0,
                attackSound = 7866,
                hitSound = 7867,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.ARMADYL, 1),
                    ),
                animationId = 10546,
                graphicId = Graphics(457),
                projectileType = Projectile.STORM_OF_ARMADYL,
                projectileId = 1019,
                endGraphic = Graphics(1019),
            ),
            Spell(
                id = 26,
                name = "Confuse",
                level = 3,
                xp = 13.0,
                attackSound = 119,
                hitSound = 121,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BODY, 1),
                    ),
                animationId = 716,
                graphicId = Graphics(102, 100),
                projectileId = 103,
                projectileType = Projectile.STUN_SPELL,
                endGraphic = Graphics(104, 100),
            ),
            Spell(
                id = 31,
                name = "Weaken",
                level = 11,
                xp = 21.0,
                attackSound = 3011,
                hitSound = 3010,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                        RuneRequirement(RuneDefinitions.Runes.BODY, 1),
                    ),
                animationId = 717,
                graphicId = Graphics(105, 100),
                projectileId = 106,
                projectileType = Projectile.STUN_SPELL,
                endGraphic = Graphics(107, 100),
            ),
            Spell(
                id = 35,
                name = "Curse",
                level = 19,
                xp = 29.0,
                attackSound = 127,
                hitSound = 126,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                        RuneRequirement(RuneDefinitions.Runes.BODY, 1),
                    ),
                animationId = 718,
                graphicId = Graphics(108, 100),
                projectileId = 109,
                projectileType = Projectile.STUN_SPELL,
                endGraphic = Graphics(110, 100),
            ),
            Spell(
                id = 75,
                name = "Vulnerability",
                level = 66,
                xp = 76.0,
                attackSound = 3009,
                hitSound = 3008,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 5),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 5),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                animationId = 716,
                graphicId = Graphics(167, 100),
                projectileId = 168,
                projectileType = Projectile.STUN_SPELL,
                endGraphic = Graphics(169, 100),
            ),
            Spell(
                id = 78,
                name = "Enfeeble",
                level = 73,
                xp = 83.0,
                attackSound = 148,
                hitSound = 150,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 8),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 8),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                animationId = 717,
                graphicId = Graphics(170, 100),
                projectileId = 171,
                projectileType = Projectile.STUN_SPELL,
                endGraphic = Graphics(172, 100),
            ),
            Spell(
                id = 82,
                name = "Stun",
                level = 80,
                xp = 90.0,
                attackSound = 3004,
                hitSound = 3005,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 12),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 12),
                        RuneRequirement(RuneDefinitions.Runes.SOUL, 1),
                    ),
                animationId = 718,
                graphicId = Graphics(173, 100),
                projectileType = Projectile.STUN_SPELL,
                projectileId = 174,
                endGraphic = Graphics(107, 100),
            ),
            Spell(
                id = 36,
                name = "Bind",
                level = 20,
                xp = 30.0,
                damage = 10,
                attackSound = 101,
                hitSound = 99,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 2),
                    ),
                animationId = 710,
                graphicId = Graphics(177, 100),
                projectileId = 178,
                projectileType = Projectile.BIND_SPELL,
                endGraphic = Graphics(179, 100),
                bind = 8,
            ),
            Spell(
                id = 55,
                name = "Snare",
                level = 50,
                xp = 60.0,
                damage = 20,
                attackSound = 3003,
                hitSound = 3002,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 4),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 4),
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 3),
                    ),
                animationId = 710,
                graphicId = Graphics(177, 100),
                projectileId = 178,
                projectileType = Projectile.BIND_SPELL,
                endGraphic = Graphics(180, 100),
                bind = 16,
            ),
            Spell(
                id = 81,
                name = "Entangle",
                level = 79,
                xp = 90.0,
                damage = 30,
                attackSound = 151,
                hitSound = 153,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 5),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 5),
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 4),
                    ),
                animationId = 710,
                graphicId = Graphics(177, 100),
                projectileId = 178,
                projectileType = Projectile.BIND_SPELL,
                endGraphic = Graphics(181, 100),
                bind = 24,
            ),
            Spell(
                id = 86,
                name = "Teleport Block",
                damage = 20,
                level = 85,
                xp = 80.0,
                attackSound = 202,
                hitSound = 203,
                type = SpellType.Combat,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.CHAOS, 1),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                        RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                    ),
                animationId = 10503,
                graphicId = Graphics(1841),
                projectileType = Projectile.TELEPORT_BLOCK,
                projectileId = 1842,
                endGraphic = Graphics(1843),
            ),
            Spell(
                id = 27,
                name = "Enchant Crossbow bolt",
                level = 4,
                xp = 0.0,
                type = SpellType.Instant,
                runes = listOf(),
                behaviour = ModernBehaviours.enchantCrossbowBolt,
            ),
            Spell(
                id = 29,
                name = "Enchant Level-1 Jewellery",
                level = 7,
                xp = 17.5,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 1),
                    ),
                behaviour = ModernBehaviours.enchantJewellery(1),
            ),
            Spell(
                id = 41,
                name = "Enchant Level-2 Jewellery",
                level = 27,
                xp = 37.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 1),
                    ),
                behaviour = ModernBehaviours.enchantJewellery(2),
            ),
            Spell(
                id = 53,
                name = "Enchant Level-3 Jewellery",
                level = 49,
                xp = 59.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 1),
                    ),
                behaviour = ModernBehaviours.enchantJewellery(3),
            ),
            Spell(
                id = 61,
                name = "Enchant Level-4 Jewellery",
                level = 57,
                xp = 67.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 10),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 1),
                    ),
                behaviour = ModernBehaviours.enchantJewellery(4),
            ),
            Spell(
                id = 76,
                name = "Enchant Level-5 Jewellery",
                level = 68,
                xp = 78.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 15),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 15),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 1),
                    ),
                behaviour = ModernBehaviours.enchantJewellery(5),
            ),
            Spell(
                id = 88,
                name = "Enchant Level-6 Jewellery",
                level = 87,
                xp = 97.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 20),
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 20),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 1),
                    ),
                behaviour = ModernBehaviours.enchantJewellery(6),
            ),
            Spell(
                id = 44,
                name = "Telekinetic Grab",
                level = 33,
                xp = 43.0,
                type = SpellType.FloorItem,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                        RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                    ),
                behaviour = ModernBehaviours.telegrab,
            ),
            Spell(
                id = 38,
                name = "Low Alchemy",
                level = 21,
                xp = 31.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 1),
                    ),
                behaviour = ModernBehaviours.lowAlchemy,
            ),
            Spell(
                id = 59,
                name = "High Alchemy",
                level = 55,
                xp = 65.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 1),
                    ),
                behaviour = ModernBehaviours.highAlchemy,
            ),
            Spell(
                id = 33,
                name = "Bones to Bananas",
                level = 15,
                xp = 25.0,
                type = SpellType.Instant,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 1),
                    ),
                behaviour = ModernBehaviours.bonesToBananas,
            ),
            Spell(
                id = 65,
                name = "Bones to Peaches",
                level = 60,
                xp = 35.5,
                type = SpellType.Instant,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 2),
                        RuneRequirement(RuneDefinitions.Runes.WATER, 4),
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 4),
                    ),
                behaviour = ModernBehaviours.bonesToPeaches,
            ),
            Spell(
                id = 50,
                name = "Superheat Item",
                level = 43,
                xp = 53.0,
                type = SpellType.Item,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 4),
                        RuneRequirement(RuneDefinitions.Runes.NATURE, 1),
                    ),
                behaviour = ModernBehaviours.superheat,
            ),
            Spell(
                id = 60,
                name = "Charge Water Orb",
                level = 56,
                xp = 66.0,
                type =
                    SpellType.ObjectSpecific(
                        objectIds = setOf(2151),
                        orbItemId = 571,
                    ),
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.WATER, 30),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 3),
                    ),
            ),
            Spell(
                id = 64,
                name = "Charge Earth Orb",
                level = 60,
                xp = 70.0,
                type =
                    SpellType.ObjectSpecific(
                        objectIds = setOf(2154),
                        orbItemId = 575,
                    ),
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.EARTH, 30),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 3),
                    ),
            ),
            Spell(
                id = 71,
                name = "Charge Fire Orb",
                level = 63,
                xp = 73.0,
                type =
                    SpellType.ObjectSpecific(
                        objectIds = setOf(2153),
                        orbItemId = 569,
                    ),
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 30),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 3),
                    ),
            ),
            Spell(
                id = 74,
                name = "Charge Air Orb",
                level = 66,
                xp = 76.0,
                type =
                    SpellType.ObjectSpecific(
                        objectIds = setOf(2152),
                        orbItemId = 573,
                    ),
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.AIR, 30),
                        RuneRequirement(RuneDefinitions.Runes.COSMIC, 3),
                    ),
            ),
            Spell(
                id = 83,
                name = "Charge",
                level = 80,
                xp = 180.0,
                type = SpellType.Instant,
                runes =
                    listOf(
                        RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                        RuneRequirement(RuneDefinitions.Runes.BLOOD, 3),
                        RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                    ),
            ),
        )
}
