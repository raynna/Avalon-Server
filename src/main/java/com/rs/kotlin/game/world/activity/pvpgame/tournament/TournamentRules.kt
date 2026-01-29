package com.rs.kotlin.game.world.activity.pvpgame.tournament

data class TournamentRules(
    val protectionPrayersAllowed: Boolean = true,
    val overheadPrayersAllowed: Boolean = true,
    val vengeanceAllowed: Boolean = true,
    val specialAttacksAllowed: Boolean = true,
    val foodAllowed: Boolean = true,
    val drinksAllowed: Boolean = true,
    val summoningAllowed: Boolean = false,
    val teleportsAllowed: Boolean = false
)