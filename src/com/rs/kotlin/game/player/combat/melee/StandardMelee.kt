package com.rs.kotlin.game.player.combat.melee

import com.rs.kotlin.game.player.combat.AttackStyle
import com.rs.kotlin.game.player.combat.WeaponStyle

object StandardMelee : MeleeData() {

    fun getDefaultWeapon(): MeleeWeapon = UNARMED

    private val UNARMED = MeleeWeapon(
        itemId = -1,
        name = "Unarmed",
        weaponStyle = WeaponStyle.UNARMED,
        attackSpeed = 4,
        animations = mapOf(
            AttackStyle.ACCURATE to 422,
            AttackStyle.AGGRESSIVE to 423,
            AttackStyle.DEFENSIVE to 422
        )
    )
    override val weapons = listOf(
        MeleeWeapon(
            itemId = 4151,
            name = "Abyssal whip",
            weaponStyle = WeaponStyle.WHIP,
            attackSpeed = 4,
            animations = mapOf(
                AttackStyle.ACCURATE to 11969,
                AttackStyle.CONTROLLED to 11970,
                AttackStyle.DEFENSIVE to 11968
            )
        ),
        MeleeWeapon(
            itemId = 1321,
            name = "Bronze Scimitar",
            weaponStyle = WeaponStyle.SCIMITAR,
            attackSpeed = 4,
            animations = mapOf(
                AttackStyle.ACCURATE to 15071,
                AttackStyle.AGGRESSIVE to 15071,
                AttackStyle.CONTROLLED to 15072,
                AttackStyle.DEFENSIVE to 15071
            )
        ),
        MeleeWeapon(
            itemId = 1307,
            name = "Iron 2h sword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            attackSpeed = 6,
            animations = mapOf(
                AttackStyle.ACCURATE to 7041,
                AttackStyle.AGGRESSIVE to 7041,
                AttackStyle.CONTROLLED to 7048,
                AttackStyle.DEFENSIVE to 7049
            )
        )
    )
}
