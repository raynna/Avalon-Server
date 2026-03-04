package com.rs.kotlin.game.player.combat.magic

object RuneDefinitions {
    const val RUNE_POUCH = 24510
    const val RUNIC_STAFF = 24203

    object Runes {
        const val AIR = 556
        const val WATER = 555
        const val EARTH = 557
        const val FIRE = 554
        const val MIND = 558
        const val NATURE = 561
        const val CHAOS = 562
        const val DEATH = 560
        const val BLOOD = 565
        const val SOUL = 566
        const val LAW = 563
        const val BODY = 559
        const val COSMIC = 564
        const val ARMADYL = 21773

        const val MUD = 4698
        const val MIST = 4695
        const val DUST = 4696
        const val LAVA = 4699
        const val STEAM = 4694
        const val SMOKE = 4697

        const val CATALYTIC = 12851
        const val ELEMENTAL = 12850
    }

    val combinationRunes =
        mapOf(
            Runes.MIST to setOf(Runes.AIR, Runes.WATER),
            Runes.DUST to setOf(Runes.AIR, Runes.EARTH),
            Runes.SMOKE to setOf(Runes.AIR, Runes.FIRE),
            Runes.MUD to setOf(Runes.WATER, Runes.EARTH),
            Runes.STEAM to setOf(Runes.WATER, Runes.FIRE),
            Runes.LAVA to setOf(Runes.EARTH, Runes.FIRE),
            Runes.ELEMENTAL to setOf(Runes.AIR, Runes.WATER, Runes.EARTH, Runes.FIRE),
            Runes.CATALYTIC to setOf(Runes.MIND, Runes.CHAOS, Runes.DEATH, Runes.BLOOD, Runes.SOUL, Runes.LAW, Runes.BODY, Runes.COSMIC),
        )

    fun getCombinationRunesFor(baseRune: Int): List<Int> =
        combinationRunes
            .filter { it.value.contains(baseRune) }
            .map { it.key }
}
