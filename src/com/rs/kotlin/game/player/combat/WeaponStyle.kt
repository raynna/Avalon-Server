package com.rs.kotlin.game.player.combat

enum class AttackBonusType(val index: Int) {
    STAB(0),
    SLASH(1),
    CRUSH(2),
    RANGE(3)
}

enum class WeaponStyle(
    val attackStyles: List<AttackStyle>,
    private val attackBonusMap: Map<AttackStyle, AttackBonusType>? = null
) {
    SHORTBOW(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        ),
        attackBonusMap = mapOf(
            AttackStyle.ACCURATE to AttackBonusType.RANGE,
            AttackStyle.RAPID to AttackBonusType.RANGE,
            AttackStyle.LONGRANGE to AttackBonusType.RANGE
        )
    ),
    LONGBOW(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        ),
        // default to RANGE for all if no map provided
    ),
    UNARMED(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.AGGRESSIVE,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            AttackStyle.ACCURATE to AttackBonusType.CRUSH,
            AttackStyle.AGGRESSIVE to AttackBonusType.CRUSH,
            AttackStyle.DEFENSIVE to AttackBonusType.CRUSH
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
            AttackStyle.ACCURATE to AttackBonusType.STAB,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.CONTROLLED to AttackBonusType.CRUSH,
            AttackStyle.DEFENSIVE to AttackBonusType.SLASH
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
            AttackStyle.ACCURATE to AttackBonusType.SLASH,
            AttackStyle.AGGRESSIVE to AttackBonusType.SLASH,
            AttackStyle.CONTROLLED to AttackBonusType.STAB,
            AttackStyle.DEFENSIVE to AttackBonusType.SLASH
        )
    ),
    WHIP(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.CONTROLLED,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            AttackStyle.ACCURATE to AttackBonusType.SLASH,
            AttackStyle.CONTROLLED to AttackBonusType.SLASH,
            AttackStyle.DEFENSIVE to AttackBonusType.SLASH
        )
    ),
    BOW(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        )
    ),
    CHINCHOMPA(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        )
    ),
    CROSSBOW(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.RAPID,
            AttackStyle.LONGRANGE
        )
        // defaults to RANGE
    ),
    HALBERD(
        attackStyles = listOf(
            AttackStyle.ACCURATE,
            AttackStyle.CONTROLLED,
            AttackStyle.DEFENSIVE
        ),
        attackBonusMap = mapOf(
            AttackStyle.ACCURATE to AttackBonusType.STAB,
            AttackStyle.CONTROLLED to AttackBonusType.SLASH,
            AttackStyle.DEFENSIVE to AttackBonusType.CRUSH
        )
    );

    fun getAttackBonusType(attackStyle: AttackStyle): AttackBonusType {
        return attackBonusMap?.get(attackStyle) ?: AttackBonusType.RANGE
    }

    companion object {
        fun getAttackBonusType(attackStyle: AttackStyle): AttackBonusType {
            return getAttackBonusType(attackStyle)
        }
    }
}
