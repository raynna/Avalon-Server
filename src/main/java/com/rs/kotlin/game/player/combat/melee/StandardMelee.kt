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
        itemId = listOf(-1),
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
        itemId = listOf(-2),
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
                if (Utils.roll(1, 3)) {
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
            itemId = listOf(4151),
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
            itemId = listOf(5698),
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
            itemId = listOf(4747, 4958, 4959, 4960, 4961, 4962),
            name = "Torag's hammers",
            weaponStyle = WeaponStyle.HAMMER,
            effect = SpecialEffect(
                execute = { context ->
                    context.attacker.animate(Animation(2068))

                    val maxHit = CombatCalculations.calculateMeleeMaxHit(context.attacker, context.defender).maxHit
                    val maxHit1 = (maxHit + 1)/2
                    val maxHit2 = (maxHit / 2)
                    val firstHit = context.rollMelee()
                    val secondHit = context.rollMelee()
                    context.hits {
                            nextHit(baseHit = firstHit, maxHit = maxHit1)
                            nextHit(baseHit = secondHit, maxHit = maxHit2)
                    }
                }
            )
        ),
        MeleeWeapon(
            itemId = listOf(3204),
            name = "Dragon halberd",
            weaponStyle = WeaponStyle.HALBERD,
            attackRange = 1,
            animations = mapOf(
                StyleKey(AttackStyle.CONTROLLED, 0) to 437,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 440,
                StyleKey(AttackStyle.DEFENSIVE, 2) to 438,
            ),
            special = SpecialAttack(
                energyCost = 30,
                accuracyMultiplier = 1.1,
                damageMultiplier = 1.1,
                execute = { context ->
                    context.attacker.animate(Animation(1203))
                    context.attacker.gfx(Graphics(282, 0, 100))
                    context.meleeHit()
                    if (context.defender.size > 1)
                    context.meleeHit()
                }
            )
        ),
        MeleeWeapon(
            itemId = listOf(1321),
            name = "Scimitar",
            weaponStyle = WeaponStyle.SCIMITAR,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 15071,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 15071,
                StyleKey(AttackStyle.CONTROLLED, 2) to 15072,
                StyleKey(AttackStyle.DEFENSIVE, 3) to 15071,
            ),
        ),
        MeleeWeapon(
            itemId = listOf(1307, 1309, 1311, 1313, 1315, 1317, 139),
            name = "2h sword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 7041,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 7041,
                StyleKey(AttackStyle.CONTROLLED, 2) to 7048,
                StyleKey(AttackStyle.DEFENSIVE, 3) to 7049,
            ),
        ),
        MeleeWeapon(
            itemId = listOf(7158),
            name = "Dragon 2h sword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 7041,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 7041,
                StyleKey(AttackStyle.CONTROLLED, 2) to 7048,
                StyleKey(AttackStyle.DEFENSIVE, 3) to 7049,
            ),
            special = SpecialAttack(
                energyCost = 1,//test
                accuracyMultiplier = 1.15,
                damageMultiplier = 1.1,//animGFX 7078 1225 - Dragon 2H Sword Special (Power Stab)
                execute = { context ->
                    context.attacker.animate(Animation(7078))
                    context.attacker.gfx(Graphics(1225, 0, 0))
                    val targets = context.getMultiAttackTargets(2, 9)
                    for (target in targets) {
                        context.meleeHit(target = target, delay = 1)
                    }
                }
            ),
        )

    )
}
