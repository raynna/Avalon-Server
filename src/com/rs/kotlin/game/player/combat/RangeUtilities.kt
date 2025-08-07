package com.rs.kotlin.game.player.combat


abstract class RangeUtilities(val id: Int) {
    companion object {
        const val STANDARD_ID = 300
        const val SPECIAL_ID = 301

        val STANDARD: StandardRanged by lazy { StandardRanged }
        val SPECIAL: SpecialRanged by lazy { SpecialRanged }

        @JvmStatic
        fun get(id: Int): RangeUtilities? = when (id) {
            STANDARD_ID -> STANDARD
            SPECIAL_ID -> SPECIAL
            else -> null
        }

        @JvmStatic
        fun getWeaponByItemId(itemId: Int): RangedWeapon? {
            return STANDARD.weapons.find { it.itemId == itemId }
                ?: SPECIAL.weapons.find { it.itemId == itemId }
        }

        @JvmStatic
        fun getAmmoByItemId(itemId: Int): RangedAmmo? {
            return STANDARD.ammunition.find { it.itemId == itemId }
                ?: SPECIAL.ammunition.find { it.itemId == itemId }
        }
    }

    abstract val weapons: List<RangedWeapon>
    abstract val ammunition: List<RangedAmmo>
}

data class RangedWeapon(
    val itemId: Int,
    val name: String,
    val weaponType: RangedWeaponType,
    val levelRequired: Int,
    val attackSpeed: Int,
    val attackRange: Int,
    val animationId: Int,
    val projectileId: Int? = null,
    val specialAttack: SpecialAttack? = null,
    val ammoType: AmmoType
)

data class RangedAmmo(
    val itemId: Int,
    val name: String,
    val levelRequired: Int,
    val damageBonus: Int,
    val projectileId: Int,
    val dropOnGround: Boolean,
    val specialEffect: SpecialEffect? = null
)

data class SpecialAttack(
    val name: String,
    val energyCost: Int,
    val damageMultiplier: Double,
    val accuracyMultiplier: Double,
    val specialProjectileId: Int? = null,
    val specialEffect: SpecialEffect? = null
)

data class SpecialEffect(
    val type: EffectType,
    val chance: Double,
    val damage: Int,
    val duration: Int? = null
)

enum class RangedWeaponType {
    SHORTBOW,
    LONGBOW,
    CROSSBOW,
    DART,
    KNIFE,
    JAVELIN,
    CHINCHOMPA,
    THROWNAXE
}

enum class AmmoType {
    ARROW,
    BOLT,
    DART,
    KNIFE,
    JAVELIN,
    CHINCHOMPA,
    THROWNAXE
}

enum class EffectType {
    POISON,
    DRAGONFIRE,
    LIFE_LEECH,
    DEFENCE_REDUCTION,
    BIND
}

object StandardRanged : RangeUtilities(STANDARD_ID) {
    override val weapons = listOf(
        // Shortbows
        RangedWeapon(
            itemId = 841,
            name = "Shortbow",
            weaponType = RangedWeaponType.SHORTBOW,
            levelRequired = 1,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 843,
            name = "Oak shortbow",
            weaponType = RangedWeaponType.SHORTBOW,
            levelRequired = 5,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        // Longbows
        RangedWeapon(
            itemId = 839,
            name = "Longbow",
            weaponType = RangedWeaponType.LONGBOW,
            levelRequired = 1,
            attackSpeed = 6,
            attackRange = 8,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 845,
            name = "Oak longbow",
            weaponType = RangedWeaponType.LONGBOW,
            levelRequired = 5,
            attackSpeed = 6,
            attackRange = 8,
            animationId = 426,
            ammoType = AmmoType.ARROW
        ),
        // Crossbows
        RangedWeapon(
            itemId = 9174,
            name = "Bronze crossbow",
            weaponType = RangedWeaponType.CROSSBOW,
            levelRequired = 1,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 423,
            ammoType = AmmoType.BOLT
        ),
        RangedWeapon(
            itemId = 9177,
            name = "Iron crossbow",
            weaponType = RangedWeaponType.CROSSBOW,
            levelRequired = 10,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 423,
            ammoType = AmmoType.BOLT
        )
    )

    override val ammunition = listOf(
        // Arrows
        RangedAmmo(
            itemId = 882,
            name = "Bronze arrow",
            levelRequired = 1,
            damageBonus = 0,
            projectileId = 10,
            dropOnGround = true
        ),
        RangedAmmo(
            itemId = 884,
            name = "Iron arrow",
            levelRequired = 1,
            damageBonus = 1,
            projectileId = 9,
            dropOnGround = true
        ),
        // Bolts
        RangedAmmo(
            itemId = 877,
            name = "Bronze bolts",
            levelRequired = 1,
            damageBonus = 0,
            projectileId = 27,
            dropOnGround = false
        ),
        RangedAmmo(
            itemId = 9140,
            name = "Iron bolts",
            levelRequired = 10,
            damageBonus = 1,
            projectileId = 28,
            dropOnGround = false
        )
    )
}

object SpecialRanged : RangeUtilities(SPECIAL_ID) {
    override val weapons = listOf(
        // Special bows
        RangedWeapon(
            itemId = 861,
            name = "Magic shortbow",
            weaponType = RangedWeaponType.SHORTBOW,
            levelRequired = 50,
            attackSpeed = 4,
            attackRange = 7,
            animationId = 1074,
            projectileId = 249,
            specialAttack = SpecialAttack(
                name = "Magic shortbow special",
                energyCost = 55,
                damageMultiplier = 1.0,
                accuracyMultiplier = 1.0,
                specialProjectileId = 249
            ),
            ammoType = AmmoType.ARROW
        ),
        RangedWeapon(
            itemId = 11235,
            name = "Dark bow",
            weaponType = RangedWeaponType.LONGBOW,
            levelRequired = 60,
            attackSpeed = 9,
            attackRange = 8,
            animationId = 426,
            specialAttack = SpecialAttack(
                name = "Dark bow special",
                energyCost = 65,
                damageMultiplier = 1.5,
                accuracyMultiplier = 1.25,
                specialEffect = SpecialEffect(
                    type = EffectType.DEFENCE_REDUCTION,
                    chance = 0.25,
                    damage = 0,
                    duration = 5
                )
            ),
            ammoType = AmmoType.ARROW
        ),
        // Crossbows
        RangedWeapon(
            itemId = 9185,
            name = "Rune crossbow",
            weaponType = RangedWeaponType.CROSSBOW,
            levelRequired = 40,
            attackSpeed = 6,
            attackRange = 7,
            animationId = 423,
            ammoType = AmmoType.BOLT
        ),
        // Chinchompas
        RangedWeapon(
            itemId = 11959,
            name = "Red chinchompa",
            weaponType = RangedWeaponType.CHINCHOMPA,
            levelRequired = 55,
            attackSpeed = 4,
            attackRange = 3,
            animationId = 2779,
            projectileId = 908,
            ammoType = AmmoType.CHINCHOMPA
        )
    )

    override val ammunition = listOf(
        // Special arrows
        RangedAmmo(
            itemId = 11212,
            name = "Dragon arrow",
            levelRequired = 60,
            damageBonus = 6,
            projectileId = 1111,
            dropOnGround = true
        ),
        // Special bolts
        RangedAmmo(
            itemId = 9242,
            name = "Ruby bolts (e)",
            levelRequired = 50,
            damageBonus = 3,
            projectileId = 27,
            dropOnGround = false,
            specialEffect = SpecialEffect(
                type = EffectType.DRAGONFIRE,
                chance = 0.1,
                damage = 20
            )
        ),
        RangedAmmo(
            itemId = 9244,
            name = "Dragonstone bolts (e)",
            levelRequired = 60,
            damageBonus = 5,
            projectileId = 27,
            dropOnGround = false,
            specialEffect = SpecialEffect(
                type = EffectType.DRAGONFIRE,
                chance = 0.2,
                damage = 30
            )
        ),
        RangedAmmo(
            itemId = 9245,
            name = "Onyx bolts (e)",
            levelRequired = 70,
            damageBonus = 7,
            projectileId = 27,
            dropOnGround = false,
            specialEffect = SpecialEffect(
                type = EffectType.LIFE_LEECH,
                chance = 0.25,
                damage = 0
            )
        ),
        // Chinchompas
        RangedAmmo(
            itemId = 11959,
            name = "Red chinchompa",
            levelRequired = 55,
            damageBonus = 0,
            projectileId = 908,
            dropOnGround = false
        )
    )
}