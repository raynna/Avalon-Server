package com.rs.kotlin.game.player.combat.magic

import com.rs.java.game.WorldTile

abstract class Spellbook(val id: Int) {
    companion object {
        const val MODERN_ID = 192
        const val ANCIENT_ID = 193

        val MODERN: ModernMagicks by lazy { ModernMagicks }
        val ANCIENT: AncientMagicks by lazy { AncientMagicks }

        @JvmStatic
        fun get(id: Int): Spellbook? = when (id) {
            MODERN_ID -> MODERN
            ANCIENT_ID -> ANCIENT
            else -> null
        }

        @JvmStatic
        fun getSpellByComponent(componentId: Int): Spell? {
            return MODERN.spells.find { it.id == componentId }
                ?: ANCIENT.spells.find { it.id == componentId }
                ?: run {
                    println("Warning: Both MODERN and ANCIENT spellbooks were null!")
                    null
                }
        }
    }
    abstract val spells: List<Spell>
    fun getSpell(spellId: Int): Spell? = ModernMagicks.spells.find { it.id == spellId }
}

object AncientMagicks : Spellbook(ANCIENT_ID) {
    override val spells = listOf(
        Spell(
            id = 48,
            name = "Home Teleport",
            level = 0,
            xp = 0.0,
            type = SpellType.Teleport,
            runes = emptyList(),
            teleportLocation = WorldTile(3087, 3496, 0)
        ),
        Spell(
            id = 40,
            name = "Paddewwa Teleport",
            level = 54,
            xp = 64.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.FIRE, 1),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1)
            ),
            teleportLocation = WorldTile(3099, 9882, 0)
        ),
        Spell(
            id = 41,
            name = "Senntisten Teleport",
            level = 60,
            xp = 70.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            teleportLocation = WorldTile(3222, 3336, 0)
        ),
        Spell(
            id = 42,
            name = "Kharyrll Teleport",
            level = 66,
            xp = 76.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1)
            ),
            teleportLocation = WorldTile(3492, 3471, 0)
        ),
        Spell(
            id = 43,
            name = "Lassar Teleport",
            level = 72,
            xp = 82.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 4)
            ),
            teleportLocation = WorldTile(3006, 3471, 0)
        ),
        Spell(
            id = 44,
            name = "Dareeyak Teleport",
            level = 78,
            xp = 88.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2)
            ),
            teleportLocation = WorldTile(2990, 3696, 0)
        ),
        Spell(
            id = 45,
            name = "Carrallangar Teleport",
            level = 84,
            xp = 94.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 2)
            ),
            teleportLocation = WorldTile(3217, 3677, 0)
        ),
        Spell(
            id = 46,
            name = "Annakarl Teleport",
            level = 90,
            xp = 100.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2)
            ),
            teleportLocation = WorldTile(3288, 3886, 0)
        ),
        Spell(
            id = 47,
            name = "Ghorrock Teleport",
            level = 96,
            xp = 106.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2)
            ),
            teleportLocation = WorldTile(2977, 3873, 0)
        ),

        Spell(
            id = 28,
            name = "Smoke Rush",
            level = 50,
            xp = 30.0,
            damage = 150,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.FIRE, 1, true),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1, true)
            ),
            animationId = 1978,
            graphicId = 384,
            projectileId = 385,
            endGraphicId = 386
        ),
        Spell(
            id = 32,
            name = "Shadow Rush",
            level = 52,
            xp = 31.0,
            damage = 160,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1, true),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            animationId = 1978,
            graphicId = 379,
            projectileId = 380,
            endGraphicId = 380
        ),
        Spell(
            id = 24,
            name = "Blood Rush",
            level = 56,
            xp = 33.0,
            damage = 170,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1)
            ),
            animationId = 1978,
            graphicId = 373,
            projectileId = 374,
            endGraphicId = 374
        ),
        Spell(
            id = 20,
            name = "Ice Rush",
            level = 58,
            xp = 34.0,
            damage = 180,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 2)
            ),
            animationId = 1978,
            graphicId = 361,
            projectileId = 362,
            endGraphicId = 362
        ),
        Spell(
            id = 36,
            name = "Miasmic Rush",
            level = 61,
            xp = 35.0,
            damage = 200,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 2),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 1),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            animationId = 10513,
            graphicId = 1845,
            projectileId = 1846,
            endGraphicId = 1847
        ),
        Spell(
            id = 30,
            name = "Smoke Burst",
            level = 62,
            xp = 36.0,
            damage = 190,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.FIRE, 2, true),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2, true)
            ),
            animationId = 1979,
            graphicId = 389,
            endGraphicId = 389,
            multi = true
        ),
        Spell(
            id = 34,
            name = "Shadow Burst",
            level = 64,
            xp = 37.0,
            damage = 200,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1, true),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            animationId = 1979,
            graphicId = 383,
            endGraphicId = 383,
            multi = true
        ),
        Spell(
            id = 26,
            name = "Blood Burst",
            level = 68,
            xp = 39.0,
            damage = 210,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2)
            ),
            animationId = 1979,
            graphicId = 376,
            endGraphicId = 376,
            multi = true
        ),
        Spell(
            id = 22,
            name = "Ice Burst",
            level = 70,
            xp = 46.0,
            damage = 220,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 4)
            ),
            animationId = 1979,
            graphicId = 363,
            projectileId = 366,
            endGraphicId = 366,
            multi = true
        ),
        Spell(
            id = 38,
            name = "Miasmic Burst",
            level = 73,
            xp = 42.0,
            damage = 240,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 4),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 2)
            ),
            animationId = 10516,
            graphicId = 1848,
            endGraphicId = 1849,
            multi = true
        ),

        Spell(
            id = 29,
            name = "Smoke Blitz",
            level = 74,
            xp = 42.0,
            damage = 230,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.FIRE, 2, true),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2, true)
            ),
            animationId = 1978,
            graphicId = 387,
            projectileId = 386,
            endGraphicId = 386
        ),
        Spell(
            id = 33,
            name = "Shadow Blitz",
            level = 76,
            xp = 43.0,
            damage = 240,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2, true),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 2)
            ),
            animationId = 1978,
            graphicId = 381,
            projectileId = 380,
            endGraphicId = 380
        ),
        Spell(
            id = 25,
            name = "Blood Blitz",
            level = 80,
            xp = 45.0,
            damage = 250,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 4)
            ),
            animationId = 1978,
            graphicId = 375,
            projectileId = 374,
            endGraphicId = 374
        ),
        Spell(
            id = 21,
            name = "Ice Blitz",
            level = 82,
            xp = 46.0,
            damage = 260,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 3)
            ),
            animationId = 1978,
            graphicId = 367,
            projectileId = 368,
            endGraphicId = 368
        ),
        Spell(
            id = 37,
            name = "Miasmic Blitz",
            level = 85,
            xp = 48.0,
            damage = 280,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 3)
            ),
            animationId = 10524,
            graphicId = 1850,
            projectileId = 1852,
            endGraphicId = 1851
        ),

        Spell(
            id = 31,
            name = "Smoke Barrage",
            level = 86,
            xp = 48.0,
            damage = 270,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.FIRE, 4, true),
                RuneRequirement(RuneDefinitions.Runes.AIR, 4, true)
            ),
            animationId = 1979,
            graphicId = 391,
            endGraphicId = 390,
            multi = true
        ),
        Spell(
            id = 35,
            name = "Shadow Barrage",
            level = 88,
            xp = 49.0,
            damage = 280,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 4, true),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 3)
            ),
            animationId = 1979,
            graphicId = 383,
            endGraphicId = 383,
            multi = true
        ),
        Spell(
            id = 27,
            name = "Blood Barrage",
            level = 92,
            xp = 51.0,
            damage = 290,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 4),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            animationId = 1979,
            graphicId = 377,
            endGraphicId = 377,
            multi = true
        ),
        Spell(
            id = 23,
            name = "Ice Barrage",
            level = 94,
            xp = 52.0,
            damage = 300,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 4),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 6)
            ),
            animationId = 1979,
            graphicId = 369,
            projectileId = 368,
            endGraphicId = 368,
            multi = true
        ),
        Spell(
            id = 39,
            name = "Miasmic Barrage",
            level = 85,
            xp = 54.0,
            damage = 320,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 4),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 4),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 4)
            ),
            animationId = 10518,
            graphicId = 1853,
            endGraphicId = 1854,
            multi = true
        )
    )
}

object ModernMagicks : Spellbook(MODERN_ID) {
    override val spells = listOf(
        Spell(
            id = 24,
            name = "Home Teleport",
            level = 0,
            xp = 0.0,
            type = SpellType.Teleport,
            runes = emptyList(),
            teleportLocation = WorldTile(3087, 3496, 0)
        ),
        Spell(
            id = 37,
            name = "Mobilising Armies Teleport",
            level = 10,
            xp = 19.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1)
            ),
            teleportLocation = WorldTile(2413, 2848, 0)
        ),
        Spell(
            id = 40,
            name = "Varrock Teleport",
            level = 25,
            xp = 35.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 1),
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.LAW, 1)
            ),
            teleportLocation = WorldTile(3212, 3424, 0)
        ),
        Spell(
            id = 43,
            name = "Lumbridge Teleport",
            level = 31,
            xp = 41.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 1),
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.LAW, 1)
            ),
            teleportLocation = WorldTile(3222, 3218, 0)
        ),
        Spell(
            id = 46,
            name = "Falador Teleport",
            level = 37,
            xp = 48.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.LAW, 1)
            ),
            teleportLocation = WorldTile(2964, 3379, 0)
        ),
        Spell(
            id = 48,
            name = "House Teleport",
            level = 40,
            xp = 25.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 1)
            ),
            teleportLocation = WorldTile(3222, 3222, 0)
        ),
        Spell(
            id = 51,
            name = "Camelot Teleport",
            level = 45,
            xp = 55.5,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                RuneRequirement(RuneDefinitions.Runes.LAW, 1)
            ),
            teleportLocation = WorldTile(2757, 3478, 0)
        ),
        Spell(
            id = 57,
            name = "Ardougne Teleport",
            level = 51,
            xp = 61.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                RuneRequirement(RuneDefinitions.Runes.LAW, 2)
            ),
            teleportLocation = WorldTile(2664, 3305, 0)
        ),
        Spell(
            id = 62,
            name = "Watchtower Teleport",
            level = 58,
            xp = 68.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                RuneRequirement(RuneDefinitions.Runes.LAW, 2)
            ),
            teleportLocation = WorldTile(2547, 3113, 2)
        ),
        Spell(
            id = 69,
            name = "Trollheim Teleport",
            level = 61,
            xp = 68.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 2),
                RuneRequirement(RuneDefinitions.Runes.LAW, 2)
            ),
            teleportLocation = WorldTile(2888, 3674, 0)
        ),
        Spell(
            id = 72,
            name = "Ape Atoll Teleport",
            level = 64,
            xp = 76.0,
            type = SpellType.Teleport,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                RuneRequirement(RuneDefinitions.Runes.LAW, 2)
            ),
            teleportLocation = WorldTile(2762, 9094, 0)
        ),
        Spell(
            id = 98,
            name = "Wind Rush",
            level = 1,
            xp = 2.5,
            damage = 10,
            type = SpellType.Combat,
            element = ElementType.Air,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 2, true)
            ),
            animationId = 14221,
            graphicId = 457,
            projectileId = 2699,
            endGraphicId = 2700
        ),
        Spell(
            id = 25,
            name = "Wind Strike",
            level = 1,
            xp = 5.5,
            damage = 20,
            type = SpellType.Combat,
            element = ElementType.Air,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 1, true),
                RuneRequirement(RuneDefinitions.Runes.MIND, 1)
            ),
            animationId = 14221,
            graphicId = 457,
            projectileId = 458,
            endGraphicId = 463
        ),
        Spell(
            id = 28,
            name = "Water Strike",
            level = 5,
            xp = 7.5,
            damage = 40,
            type = SpellType.Combat,
            element = ElementType.Water,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                RuneRequirement(RuneDefinitions.Runes.MIND, 1)
            ),
            animationId = 14221,
            graphicId = 2702,
            projectileId = 2703,
            endGraphicId = 2708
        ),
        Spell(
            id = 30,
            name = "Earth Strike",
            level = 9,
            xp = 9.5,
            damage = 60,
            type = SpellType.Combat,
            element = ElementType.Earth,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                RuneRequirement(RuneDefinitions.Runes.MIND, 1)
            ),
            animationId = 14221,
            graphicId = 2713,
            projectileId = 2718,
            endGraphicId = 2723
        ),
        Spell(
            id = 32,
            name = "Fire Strike",
            level = 13,
            xp = 11.5,
            damage = 80,
            type = SpellType.Combat,
            element = ElementType.Fire,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                RuneRequirement(RuneDefinitions.Runes.MIND, 1)
            ),
            animationId = 14221,
            graphicId = 2728,
            projectileId = 2729,
            endGraphicId = 2737
        ),
        Spell(
            id = 34,
            name = "Wind Bolt",
            level = 17,
            xp = 13.5,
            damage = 90,
            type = SpellType.Combat,
            element = ElementType.Air,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 1)
            ),
            animationId = 14221,
            graphicId = 2699,
            projectileId = 2699,
            endGraphicId = 2700
        ),
        Spell(
            id = 39,
            name = "Water Bolt",
            level = 23,
            xp = 16.5,
            damage = 100,
            type = SpellType.Combat,
            element = ElementType.Water,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 1)
            ),
            animationId = 14221,
            graphicId = 2707,
            projectileId = 2704,
            endGraphicId = 2709
        ),
        Spell(
            id = 42,
            name = "Earth Bolt",
            level = 29,
            xp = 19.5,
            damage = 110,
            type = SpellType.Combat,
            element = ElementType.Earth,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 1)
            ),
            animationId = 14221,
            graphicId = 2714,
            projectileId = 2719,
            endGraphicId = 2724
        ),
        Spell(
            id = 45,
            name = "Fire Bolt",
            level = 35,
            xp = 22.5,
            damage = 120,
            type = SpellType.Combat,
            element = ElementType.Fire,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 4),
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 1)
            ),
            animationId = 14221,
            graphicId = 2728,
            projectileId = 2731,
            endGraphicId = 2738
        ),
        Spell(
            id = 47,
            name = "Crumble Undead",
            level = 39,
            xp = 24.5,
            damage = 160,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 2),
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 1)
            ),
            animationId = 724,
            graphicId = 145,
            projectileId = 146,
            endGraphicId = 147
        ),
        Spell(
            id = 49,
            name = "Wind Blast",
            level = 41,
            xp = 25.5,
            damage = 130,
            type = SpellType.Combat,
            element = ElementType.Air,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 14221,
            graphicId = 2699,
            projectileId = 2699,
            endGraphicId = 2700
        ),
        Spell(
            id = 52,
            name = "Water Blast",
            level = 47,
            xp = 28.5,
            damage = 140,
            type = SpellType.Combat,
            element = ElementType.Water,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 14221,
            graphicId = 2701,
            projectileId = 2705,
            endGraphicId = 2710
        ),
        Spell(
            id = 54,
            name = "Iban Blast",
            level = 50,
            xp = 30.0,
            damage = 250,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 708,
            graphicId = 87,
            projectileId = 88,
            endGraphicId = 89
        ),
        Spell(
            id = 56,
            name = "Magic Dart",
            level = 50,
            xp = 30.0,
            damage = 0,//special calc for this
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1),
                RuneRequirement(RuneDefinitions.Runes.MIND, 4)
            ),
            animationId = 1575,
            projectileId = 328,
            endGraphicId = 329
        ),
        Spell(
            id = 58,
            name = "Earth Blast",
            level = 53,
            xp = 31.5,
            damage = 150,
            type = SpellType.Combat,
            element = ElementType.Earth,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 4),
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 14221,
            graphicId = 2715,
            projectileId = 2720,
            endGraphicId = 2725
        ),
        Spell(
            id = 63,
            name = "Fire Blast",
            level = 59,
            xp = 34.5,
            damage = 160,
            type = SpellType.Combat,
            element = ElementType.Fire,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                RuneRequirement(RuneDefinitions.Runes.AIR, 4),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 14221,
            graphicId = 2728,
            projectileId = 2733,
            endGraphicId = 2739
        ),
        Spell(
            id = 66,
            name = "Saradomin Strike",
            level = 60,
            xp = 35.0,
            damage = 200,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 2),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 4)
            ),
            animationId = 811,
            endGraphicId = 76
        ),
        Spell(
            id = 67,
            name = "Claws of Guthix",
            level = 60,
            xp = 35.0,
            damage = 200,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 1),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 4)
            ),
            animationId = 811,
            endGraphicId = 77
        ),
        Spell(
            id = 68,
            name = "Flames of Zamorak",
            level = 60,
            xp = 35.0,
            damage = 200,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 4),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 2),
                RuneRequirement(RuneDefinitions.Runes.AIR, 1)
            ),
            animationId = 811,
            endGraphicId = 78
        ),
        Spell(
            id = 70,
            name = "Wind Wave",
            level = 62,
            xp = 36.0,
            damage = 170,
            type = SpellType.Combat,
            element = ElementType.Air,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1)
            ),
            animationId = 14221,
            graphicId = 2699,
            projectileId = 2699,
            endGraphicId = 2700
        ),
        Spell(
            id = 73,
            name = "Water Wave",
            level = 65,
            xp = 37.5,
            damage = 180,
            type = SpellType.Combat,
            element = ElementType.Water,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 7),
                RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1)
            ),
            animationId = 14221,
            graphicId = 2702,
            projectileId = 2706,
            endGraphicId = 2710
        ),
        Spell(
            id = 77,
            name = "Earth Wave",
            level = 70,
            xp = 40.0,
            damage = 190,
            type = SpellType.Combat,
            element = ElementType.Earth,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 7),
                RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1)
            ),
            animationId = 14221,
            graphicId = 2716,
            projectileId = 2721,
            endGraphicId = 2726
        ),
        Spell(
            id = 80,
            name = "Fire Wave",
            level = 75,
            xp = 42.5,
            damage = 200,
            type = SpellType.Combat,
            element = ElementType.Fire,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 7),
                RuneRequirement(RuneDefinitions.Runes.AIR, 5),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1)
            ),
            animationId = 14223,
            graphicId = 2728,
            projectileId = 2735,
            endGraphicId = 2740
        ),
        Spell(
            id = 84,
            name = "Wind Surge",
            level = 81,
            xp = 44.5,
            damage = 220,
            type = SpellType.Combat,
            element = ElementType.Air,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 7),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 10546,
            graphicId = 457,
            projectileId = 462,
            endGraphicId = 2700
        ),
        Spell(
            id = 87,
            name = "Water Surge",
            level = 85,
            xp = 46.5,
            damage = 240,
            type = SpellType.Combat,
            element = ElementType.Water,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 10),
                RuneRequirement(RuneDefinitions.Runes.AIR, 7),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 10542,
            graphicId = 2701,
            projectileId = 2707,
            endGraphicId = 2712
        ),
        Spell(
            id = 89,
            name = "Earth Surge",
            level = 90,
            xp = 48.5,
            damage = 260,
            type = SpellType.Combat,
            element = ElementType.Earth,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 10),
                RuneRequirement(RuneDefinitions.Runes.AIR, 7),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 14209,
            graphicId = 2717,
            projectileId = 2722,
            endGraphicId = 2727
        ),
        Spell(
            id = 91,
            name = "Fire Surge",
            level = 95,
            xp = 50.5,
            damage = 280,
            type = SpellType.Combat,
            element = ElementType.Fire,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 10),
                RuneRequirement(RuneDefinitions.Runes.AIR, 10),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 1),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            projectileIds = listOf(2736, 2735, 2736),
            animationId = 2791,
            graphicId = 2728,
            endGraphicId = 2741
        ),
        Spell(
            id = 99,
            name = "Storm of Armadyl",
            level = 77,
            xp = 70.0,
            damage = 0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.ARMADYL, 1)
            ),
            animationId = 10546,
            graphicId = 457,
            projectileId = 1019,
            endGraphicId = 1019
        ),

        Spell(
            id = 26,
            name = "Confuse",
            level = 3,
            xp = 13.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                RuneRequirement(RuneDefinitions.Runes.BODY, 1)
            ),
            animationId = 710,
            graphicId = 102,
            projectileId = 103,
            endGraphicId = 104
        ),
        Spell(
            id = 31,
            name = "Weaken",
            level = 11,
            xp = 21.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                RuneRequirement(RuneDefinitions.Runes.BODY, 1)
            ),
            animationId = 710,
            graphicId = 105,
            projectileId = 106,
            endGraphicId = 107
        ),
        Spell(
            id = 35,
            name = "Curse",
            level = 19,
            xp = 29.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                RuneRequirement(RuneDefinitions.Runes.BODY, 1)
            ),
            animationId = 710,
            graphicId = 108,
            projectileId = 109,
            endGraphicId = 110
        ),
        Spell(
            id = 75,
            name = "Vulnerability",
            level = 66,
            xp = 76.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 5),
                RuneRequirement(RuneDefinitions.Runes.WATER, 5),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            animationId = 710,
            graphicId = 167,
            projectileId = 168,
            endGraphicId = 169
        ),
        Spell(
            id = 78,
            name = "Enfeeble",
            level = 73,
            xp = 83.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 8),
                RuneRequirement(RuneDefinitions.Runes.WATER, 8),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            animationId = 710,
            graphicId = 170,
            projectileId = 171,
            endGraphicId = 172
        ),
        Spell(
            id = 82,
            name = "Stun",
            level = 80,
            xp = 90.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 12),
                RuneRequirement(RuneDefinitions.Runes.WATER, 12),
                RuneRequirement(RuneDefinitions.Runes.SOUL, 1)
            ),
            animationId = 710,
            graphicId = 173,
            projectileId = 174,
            endGraphicId = 107
        ),
        Spell(
            id = 36,
            name = "Bind",
            level = 20,
            xp = 30.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 3),
                RuneRequirement(RuneDefinitions.Runes.WATER, 3),
                RuneRequirement(RuneDefinitions.Runes.NATURE, 2)
            ),
            animationId = 710,
            graphicId = 177,
            projectileId = 178,
            endGraphicId = 179
        ),
        Spell(
            id = 55,
            name = "Snare",
            level = 50,
            xp = 60.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 4),
                RuneRequirement(RuneDefinitions.Runes.WATER, 4),
                RuneRequirement(RuneDefinitions.Runes.NATURE, 3)
            ),
            animationId = 710,
            graphicId = 177,
            projectileId = 178,
            endGraphicId = 180
        ),
        Spell(
            id = 81,
            name = "Entangle",
            level = 79,
            xp = 90.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 5),
                RuneRequirement(RuneDefinitions.Runes.WATER, 5),
                RuneRequirement(RuneDefinitions.Runes.NATURE, 4)
            ),
            animationId = 710,
            graphicId = 177,
            projectileId = 178,
            endGraphicId = 181
        ),
        Spell(
            id = 86,
            name = "Teleport Block",
            level = 85,
            xp = 80.0,
            type = SpellType.Combat,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.CHAOS, 1),
                RuneRequirement(RuneDefinitions.Runes.LAW, 1),
                RuneRequirement(RuneDefinitions.Runes.DEATH, 1)
            ),
            animationId = 10503,
            graphicId = 1841,
            projectileId = 1842,
            endGraphicId = 1843
        ),

        Spell(
            id = 29,
            name = "Enchant Level-1 Jewellery",
            level = 7,
            xp = 17.5,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 1),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 1)
            )
        ),
        Spell(
            id = 41,
            name = "Enchant Level-2 Jewellery",
            level = 27,
            xp = 37.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 3),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 1)
            )
        ),
        Spell(
            id = 53,
            name = "Enchant Level-3 Jewellery",
            level = 49,
            xp = 59.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 1)
            )
        ),
        Spell(
            id = 61,
            name = "Enchant Level-4 Jewellery",
            level = 57,
            xp = 67.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 10),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 1)
            )
        ),
        Spell(
            id = 76,
            name = "Enchant Level-5 Jewellery",
            level = 68,
            xp = 78.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 15),
                RuneRequirement(RuneDefinitions.Runes.WATER, 15),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 1)
            )
        ),
        Spell(
            id = 88,
            name = "Enchant Level-6 Jewellery",
            level = 87,
            xp = 97.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 20),
                RuneRequirement(RuneDefinitions.Runes.FIRE, 20),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 1)
            )
        ),

        Spell(
            id = 44,
            name = "Telegrab",
            level = 33,
            xp = 0.0,
            type = SpellType.FloorItem,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 1),
                RuneRequirement(RuneDefinitions.Runes.LAW, 1)
            )
        ),
        Spell(
            id = 38,
            name = "Low Alchemy",
            level = 21,
            xp = 31.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                RuneRequirement(RuneDefinitions.Runes.NATURE, 1)
            )
        ),
        Spell(
            id = 59,
            name = "High Alchemy",
            level = 55,
            xp = 65.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 5),
                RuneRequirement(RuneDefinitions.Runes.NATURE, 1)
            )
        ),
        Spell(
            id = 33,
            name = "Bones to Bananas",
            level = 15,
            xp = 25.0,
            type = SpellType.Instant,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 2),
                RuneRequirement(RuneDefinitions.Runes.NATURE, 1)
            )
        ),
        Spell(
            id = 65,
            name = "Bones to Peaches",
            level = 60,
            xp = 35.5,
            type = SpellType.Instant,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.NATURE, 2),
                RuneRequirement(RuneDefinitions.Runes.WATER, 4),
                RuneRequirement(RuneDefinitions.Runes.EARTH, 4)
            )
        ),
        Spell(
            id = 50,
            name = "Superheat Item",
            level = 43,
            xp = 53.0,
            type = SpellType.Item,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 4),
                RuneRequirement(RuneDefinitions.Runes.NATURE, 1)
            )
        ),
        Spell(
            id = 60,
            name = "Charge Water Orb",
            level = 56,
            xp = 66.0,
            type = SpellType.Object,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.WATER, 30),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 3)
            )
        ),
        Spell(
            id = 64,
            name = "Charge Earth Orb",
            level = 60,
            xp = 70.0,
            type = SpellType.Object,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.EARTH, 30),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 3)
            )
        ),
        Spell(
            id = 71,
            name = "Charge Fire Orb",
            level = 63,
            xp = 73.0,
            type = SpellType.Object,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 30),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 3)
            )
        ),
        Spell(
            id = 74,
            name = "Charge Air Orb",
            level = 66,
            xp = 76.0,
            type = SpellType.Object,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.AIR, 30),
                RuneRequirement(RuneDefinitions.Runes.COSMIC, 3)
            )
        ),
        Spell(
            id = 83,
            name = "Charge",
            level = 80,
            xp = 180.0,
            type = SpellType.Instant,
            runes = listOf(
                RuneRequirement(RuneDefinitions.Runes.FIRE, 3),
                RuneRequirement(RuneDefinitions.Runes.BLOOD, 3),
                RuneRequirement(RuneDefinitions.Runes.AIR, 3)
            )
        )
    )
}