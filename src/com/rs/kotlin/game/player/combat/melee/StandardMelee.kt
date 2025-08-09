package com.rs.kotlin.game.player.combat.melee

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.kotlin.game.player.combat.*

object StandardMelee : MeleeData() {

    fun getDefaultWeapon(): MeleeWeapon = UNARMED

    private val UNARMED = MeleeWeapon(
        itemId = -1,
        name = "Unarmed",
        weaponStyle = WeaponStyle.UNARMED,
        attackSpeed = 4,
        animations = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to 422,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to 423,
            StyleKey(AttackStyle.DEFENSIVE, 2) to 422,
        ),
        specialAttack = SpecialAttack(
            energyCost = 25,
            accuracyMultiplier = 1.15,
            damageMultiplier = 1.15,
            execute = { context ->
                val hit1 = context.combat.registerHit(context.attacker, context.defender, CombatType.MELEE, context.attackStyle, context.weapon)
                val hit2 = context.combat.registerHit(context.attacker, context.defender, CombatType.MELEE, context.attackStyle, context.weapon)
                context.combat.delayHits(
                    PendingHit(hit1, 0),
                    PendingHit(hit2, 0),
                )
            }
        )
    )
    override val weapons = listOf(
        MeleeWeapon(
            itemId = 4151,
            name = "Abyssal whip",
            weaponStyle = WeaponStyle.WHIP,
            attackSpeed = 4,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 11969,
                StyleKey(AttackStyle.CONTROLLED, 1) to 11970,
                StyleKey(AttackStyle.DEFENSIVE, 2) to 11968,
            ),
            specialAttack = SpecialAttack(
                energyCost = 25,
                accuracyMultiplier = 1.15,
                damageMultiplier = 1.15,
                execute = { context ->
                    context.attacker.animate(Animation(11971))
                    context.defender.gfx(Graphics(2108, 0, 100))
                    val hit = context.combat.registerHit(context.attacker, context.defender, CombatType.MELEE, context.attackStyle, context.weapon)
                    val hit2 = hit.copyWithDamage(hit.damage/2).setLook(Hit.HitLook.MAGIC_DAMAGE)
                    val hit3 = hit2.copyWithDamage(hit2.damage/2).setLook(Hit.HitLook.RANGE_DAMAGE)
                    context.combat.delayHits(
                        PendingHit(hit, 0),
                        PendingHit(hit2, 1),
                        PendingHit(hit3, 1),
                    )
                }
            )
        ),
        MeleeWeapon(
            itemId = 5698,
            name = "Dragon dagger",
            weaponStyle = WeaponStyle.DAGGER,
            attackSpeed = 4,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 376,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 376,
                StyleKey(AttackStyle.AGGRESSIVE, 2) to 377,
                StyleKey(AttackStyle.DEFENSIVE, 3) to 376,
            ),
            specialAttack = SpecialAttack(
                energyCost = 25,
                accuracyMultiplier = 1.15,
                damageMultiplier = 1.15,
                execute = { context ->
                    val special = context.weapon.specialAttack!!
                    context.attacker.animate(Animation(1062))
                    context.attacker.gfx(Graphics(252, 0, 100))
                    fun registerSpecialHit() = context.combat.registerHit(
                        context.attacker,
                        context.defender,
                        CombatType.MELEE,
                        context.attackStyle,
                        context.weapon,
                        accuracyMultiplier = special.accuracyMultiplier,
                        damageMultiplier = special.damageMultiplier
                    )
                    context.combat.delayHits(
                        PendingHit(registerSpecialHit(), 0),
                        PendingHit(registerSpecialHit(), 1)
                    )
                }
            )
        ),
        MeleeWeapon(
            itemId = 1321,
            name = "Bronze Scimitar",
            weaponStyle = WeaponStyle.SCIMITAR,
            attackSpeed = 4,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 15071,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 15071,
                StyleKey(AttackStyle.CONTROLLED, 2) to 15072,
                StyleKey(AttackStyle.DEFENSIVE, 3) to 15071,
            ),
        ),
        MeleeWeapon(
            itemId = 1307,
            name = "Iron 2h sword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            attackSpeed = 6,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 7041,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 7041,
                StyleKey(AttackStyle.CONTROLLED, 2) to 7048,
                StyleKey(AttackStyle.DEFENSIVE, 3) to 7049,
            ),
        )
    )
}
