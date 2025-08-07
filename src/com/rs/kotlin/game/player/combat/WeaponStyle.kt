package com.rs.kotlin.game.player.combat

enum class WeaponStyle(val attackStyles: List<AttackStyle>) {
    SHORTBOW(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.RAPID,
        AttackStyle.LONGRANGE
    )),
    LONGBOW(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.RAPID,
        AttackStyle.LONGRANGE
    )),
    UNARMED(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.AGGRESSIVE,
        AttackStyle.DEFENSIVE
    )),

    SCIMITAR(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.AGGRESSIVE,
        AttackStyle.CONTROLLED,
        AttackStyle.DEFENSIVE
    )),

    WHIP(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.CONTROLLED,
        AttackStyle.DEFENSIVE
    )),

    BOW(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.RAPID,
        AttackStyle.LONGRANGE
    )),

    CHINCHOMPA(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.RAPID,
        AttackStyle.LONGRANGE
    )),

    CROSSBOW(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.RAPID,
        AttackStyle.LONGRANGE
    )),

    HALBERD(listOf(
        AttackStyle.ACCURATE,
        AttackStyle.CONTROLLED,
        AttackStyle.DEFENSIVE
    ))
}
