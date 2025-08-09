package com.rs.kotlin.game.player.combat

enum class AttackBonusType(val index: Int) {
    STAB(0),
    SLASH(1),
    CRUSH(2),
    RANGE(3)
}

data class StyleKey(val style: AttackStyle, val index: Int)

enum class WeaponStyle(
    val attackStyles: List<AttackStyle>,
    val attackBonusMap: Map<StyleKey, AttackBonusType>
) {
    SHORTBOW(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.RAPID, 1) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.LONGRANGE, 2) to AttackBonusType.RANGE,
        )
    ),
    CROSSBOW(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.RAPID, 1) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.LONGRANGE, 2) to AttackBonusType.RANGE,
        )
    ),
    THROWING(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.RAPID, 1) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.LONGRANGE, 2) to AttackBonusType.RANGE,
        )
    ),
    LONGBOW(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.RAPID, 1) to AttackBonusType.RANGE,
            StyleKey(AttackStyle.LONGRANGE, 2) to AttackBonusType.RANGE,
        )
    ),
    UNARMED(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.AGGRESSIVE,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.CRUSH,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to AttackBonusType.CRUSH,
            StyleKey(AttackStyle.DEFENSIVE, 2) to AttackBonusType.CRUSH,
        )
    ),
    TWO_HANDED_SWORD(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.AGGRESSIVE,
            AttackStyle.CONTROLLED,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.STAB,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to AttackBonusType.SLASH,
            StyleKey(AttackStyle.CONTROLLED, 2) to AttackBonusType.CRUSH,
            StyleKey(AttackStyle.DEFENSIVE, 3) to AttackBonusType.SLASH,
        )
    ),
    SCIMITAR(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.AGGRESSIVE,
            AttackStyle.CONTROLLED,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.SLASH,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to AttackBonusType.SLASH,
            StyleKey(AttackStyle.CONTROLLED, 2) to AttackBonusType.STAB,
            StyleKey(AttackStyle.DEFENSIVE, 3) to AttackBonusType.SLASH,
        )
    ),
    DAGGER(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.AGGRESSIVE,
            AttackStyle.AGGRESSIVE,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.STAB,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to AttackBonusType.STAB,
            StyleKey(AttackStyle.AGGRESSIVE, 2) to AttackBonusType.SLASH,
            StyleKey(AttackStyle.DEFENSIVE, 3) to AttackBonusType.STAB,
        )
    ),
    WHIP(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.CONTROLLED,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to AttackBonusType.SLASH,
            StyleKey(AttackStyle.CONTROLLED, 1) to AttackBonusType.SLASH,
            StyleKey(AttackStyle.DEFENSIVE, 2) to AttackBonusType.SLASH,
        )
    ),
    HALBERD(
        attackStyles = listOf(
            AttackStyle.CONTROLLED,
            AttackStyle.AGGRESSIVE,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            StyleKey(AttackStyle.CONTROLLED, 0) to AttackBonusType.STAB,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to AttackBonusType.SLASH,
            StyleKey(AttackStyle.DEFENSIVE, 2) to AttackBonusType.STAB,
        )
    );

    fun getAttackBonusType(attackStyle: AttackStyle, styleIndex: Int): AttackBonusType {
        return attackBonusMap[StyleKey(attackStyle, styleIndex)] ?: AttackBonusType.RANGE
    }

    companion object {
    }
}
