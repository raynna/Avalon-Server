package com.rs.kotlin.game.player.combat

enum class AttackBonusType(val index: Int) {
    STAB(0),
    SLASH(1),
    CRUSH(2),
    RANGE(3)
}

data class StyleSet(
    val styles: List<AttackStyle>,
    val bonuses: List<AttackBonusType>) {
        fun styleAt(index: Int) = styles.getOrNull(index)
        fun bonusAt(index: Int) = bonuses.getOrNull(index)
    }

fun styles(vararg pairs: Pair<AttackStyle, AttackBonusType>) =
    StyleSet(
        styles = pairs.map { it.first },
        bonuses = pairs.map { it.second }
    )


data class StyleKey(val style: AttackStyle, val index: Int)

enum class WeaponStyle(
    val styleSet: StyleSet
) {
    SHORTBOW(
        styles(
            AttackStyle.ACCURATE_RANGE to AttackBonusType.RANGE,
            AttackStyle.RAPID to AttackBonusType.RANGE,
            AttackStyle.LONGRANGE to AttackBonusType.RANGE
        )
    ),
    CROSSBOW(
        styles(
            AttackStyle.ACCURATE_RANGE to AttackBonusType.RANGE,
            AttackStyle.RAPID to AttackBonusType.RANGE,
            AttackStyle.LONGRANGE to AttackBonusType.RANGE
        )
    ),
    THROWING(
        styles(
            AttackStyle.ACCURATE_RANGE to AttackBonusType.RANGE,
            AttackStyle.RAPID to AttackBonusType.RANGE,
            AttackStyle.LONGRANGE to AttackBonusType.RANGE
        )
    ),
    LONGBOW(
        styles(
            AttackStyle.ACCURATE_RANGE to AttackBonusType.RANGE,
            AttackStyle.RAPID to AttackBonusType.RANGE,
            AttackStyle.LONGRANGE to AttackBonusType.RANGE
        )
    ),
    UNARMED(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.CRUSH,
            AttackStyle.AGGRESSIVE to AttackBonusType.CRUSH,
            AttackStyle.DEFENSIVE to AttackBonusType.CRUSH
        )
    ),
    TWO_HANDED_SWORD(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.SLASH,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.AGGRESSIVE to AttackBonusType.CRUSH,
            AttackStyle.DEFENSIVE to AttackBonusType.SLASH
        ),
    ),
    SCIMITAR(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.SLASH,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.CONTROLLED to AttackBonusType.STAB,
            AttackStyle.DEFENSIVE to AttackBonusType.SLASH
        ),
    ),
    DAGGER(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.STAB,
            AttackStyle.AGGRESSIVE to AttackBonusType.STAB,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.DEFENSIVE to AttackBonusType.STAB
        ),
    ),
    CLAWS(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.SLASH,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.CONTROLLED to AttackBonusType.STAB,
            AttackStyle.DEFENSIVE to AttackBonusType.SLASH
        ),
    ),
    WHIP(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.SLASH,
            AttackStyle.CONTROLLED to AttackBonusType.SLASH,
            AttackStyle.DEFENSIVE to AttackBonusType.SLASH
        ),
    ),
    HALBERD(
        styles(
            AttackStyle.CONTROLLED to AttackBonusType.STAB,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.DEFENSIVE to AttackBonusType.STAB
        ),
    ),
    STAFF_OF_LIGHT(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.STAB,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.DEFENSIVE to AttackBonusType.CRUSH,
        ),
    ),
    STAFF(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.CRUSH,
            AttackStyle.AGGRESSIVE to AttackBonusType.CRUSH,
            AttackStyle.DEFENSIVE to AttackBonusType.CRUSH,
        ),
    ),
    HAMMER(
        styles(
            AttackStyle.ACCURATE to AttackBonusType.CRUSH,
            AttackStyle.AGGRESSIVE to AttackBonusType.CRUSH,
            AttackStyle.DEFENSIVE to AttackBonusType.CRUSH
        ),
    );

    companion object {
    }
}
