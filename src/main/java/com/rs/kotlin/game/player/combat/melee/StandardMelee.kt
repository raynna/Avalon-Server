package com.rs.kotlin.game.player.combat.melee

import com.rs.java.game.npc.NPC;
import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.*
import com.rs.kotlin.game.player.combat.special.*

object StandardMelee : MeleeData() {

    fun getDefaultWeapon(): MeleeWeapon = UNARMED
    fun getGoliathWeapon(): MeleeWeapon = GOLIATH_GLOVES

    private val UNARMED = MeleeWeapon(
        itemId = -1,
        name = "Unarmed",
        weaponStyle = WeaponStyle.UNARMED,
        attackSpeed = 4,
        animations = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to 422,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to 423,
            StyleKey(AttackStyle.DEFENSIVE, 2) to 422,
        )
    )
    private val GOLIATH_GLOVES = MeleeWeapon(
        itemId = -2,
        name = "Goliath gloves",//anims 14307 && 14393 && effect = 14417
        weaponStyle = WeaponStyle.UNARMED,
        attackSpeed = 4,
        animations = mapOf(
            StyleKey(AttackStyle.ACCURATE, 0) to 14307,
            StyleKey(AttackStyle.AGGRESSIVE, 1) to 14393,
            StyleKey(AttackStyle.DEFENSIVE, 2) to 14307,
        ),
        effect = SpecialEffect(
            execute = { context ->
                if (Utils.roll(1, 15)) {
                    context.defender.addFreezeDelay(16, false);
                    context.defender.gfx( Graphics(181, 0, 96))
                    context.forcedHit(delay = 1)
                } else {
                    context.meleeHit()
                }
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
            special = SpecialAttack(
                energyCost = 25,
                accuracyMultiplier = 1.15,
                damageMultiplier = 1.15,
                execute = { context ->
                    context.attacker.animate(Animation(11971))
                    context.defender.gfx(Graphics(2108, 0, 100))
                    context.meleeHit(1.15, 1.15, Hit.HitLook.MAGIC_DAMAGE)//melee roll with magic damage
                    context.meleeHit(1.15, 1.15)//melee roll with melee damage

                    context.rangedHit()//range roll with range damage, no multipliers

                    //context.magic(1.0, hitLook = Hit.HitLook.MELEE_DAMAGE)//magic roll with melee damage

                    context.repeatHits(2)

                    context.repeatHits(
                        combatType = CombatType.MELEE,
                        delays = listOf(0, 1),
                        accuracyMultipliers = listOf(1.15, 1.15),
                        damageMultipliers = listOf(1.15, 1.15)
                    )

                    context.hits {
                        melee(1.15, 1.15, delay = 0)
                        melee(1.15, 1.15, delay = 1)
                    }

                    context.hits {
                        val melee = melee(1.25, 1.25, delay = 0)
                        nextHit(melee, scale = 0.5, delay = 1)
                        nextHit(melee, scale = 0.5, delay = 2)
                        nextHit(melee, scale = 1.0, delay = 2)
                    }
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
            special = SpecialAttack(
                energyCost = 25,
                accuracyMultiplier = 1.15,
                damageMultiplier = 1.15,
                execute = { context ->
                    context.attacker.animate(Animation(1062))
                    context.attacker.gfx(Graphics(252, 0, 100))
                    context.meleeHit()
                    context.meleeHit(delay = if (context.defender is NPC) 1 else 0)
                }
            )
        ),
        MeleeWeapon(
            itemId = 4747,
            name = "Torag's hammers",
            weaponStyle = WeaponStyle.HAMMER,
            effect = SpecialEffect(
                execute = { context ->
                    context.attacker.animate(Animation(2068))
                    context.meleeHit(delay = 0, damageMultiplier = 0.75)
                    context.meleeHit(delay = 0, damageMultiplier = 0.75)
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
