package com.rs.kotlin.game.player.combat.melee

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.Hit
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.game.player.TickManager
import com.rs.java.utils.Utils
import com.rs.kotlin.Rscm
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
                    context.defender.gfx(Graphics(181, 0, 96))
                    context.forcedHit(delay = 1)
                } else {
                    context.meleeHit()
                }
            }
        )
    )
    override val weapons = listOf(
        MeleeWeapon(
            itemId = Weapon.itemIds("item.dragon_claws", "item.dragon_claws_2", "item.lucky_dragon_claws"),
            name = "Dragon claws",
            weaponStyle = WeaponStyle.CLAWS,
            blockAnimationId = Animation.getId("animation.claws_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.claws_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.claws_slash"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.claws_stab"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.claws_slash"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 50,
                execute = { context ->
                    context.attacker.animate("animation.dragon_claws_special")
                    context.attacker.gfx("graphic.dragon_claws_special")
                    for (delay in 15..60 step 15) {//TODO get rid of magic number for sounds
                        context.attacker.packets.sendSound(7464, delay, 1)
                    }
                    val dragonClawsHits = context.getDragonClawsHits(4)
                    val firstHitIndex = dragonClawsHits.indexOfFirst { it.damage > 9 }
                    context.hits {
                        dragonClawsHits.forEachIndexed { i, hit ->
                            val delay = if (firstHitIndex == -1) 0 else i / 2
                            addHit(hit = hit.copy(), delay = delay)
                        }
                    }
                }
            )
        ),
        MeleeWeapon(
            itemId = Weapon.itemIds("item.korasi_sword"),
            name = "Korasi's sword",
            weaponStyle = WeaponStyle.SCIMITAR,
            blockAnimationId = Animation.getId("animation.scimitar_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.scimitar_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.scimitar_slash"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.scimitar_stab"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.scimitar_slash"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 60,
                execute = { context ->
                    context.attacker.animate(14788)
                    context.attacker.gfx(1729)
                    context.attacker.playSound(3853, 1)
                    context.attacker.playSound(3865, 1)
                    val maxHit = CombatCalculations.calculateMeleeMaxHit(context.attacker, context.defender).maxHit

                    val isMultiCombat = context.defender.isAtMultiArea

                    val firstHitDamage = if (isMultiCombat) {
                        (0..((1.5 * maxHit).toInt())).random()
                    } else {
                        ((0.5 * maxHit).toInt() .. (1.5 * maxHit).toInt()).random()
                    }

                    val firstHit = Hit(context.attacker, firstHitDamage, Hit.HitLook.MAGIC_DAMAGE)
                    if (firstHit.checkCritical(firstHitDamage, maxHit))
                        firstHit.critical = true
                    context.hits {
                        addHit(context.defender, firstHit)
                        context.defender.gfx(1730)
                        if (isMultiCombat) {
                            val extraTargets = context.getMultiAttackTargets(maxDistance = 1, maxTargets = 2)
                            val damages = listOf(firstHitDamage / 2, firstHitDamage / 4)

                            for ((index, target) in extraTargets.withIndex()) {
                                if (index >= damages.size) break
                                target.gfx(1730)
                                addHit(target, firstHit.copyWithDamage(damages[index]), delay = 0)
                            }
                        }
                    }
                }
            )
        ),
        MeleeWeapon(
            itemId = Weapon.itemIds("item.abyssal_whip"),
            name = "Abyssal whip",
            weaponStyle = WeaponStyle.WHIP,
            blockAnimationId = 11974,
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to 11969,
                StyleKey(AttackStyle.CONTROLLED, 1) to 11970,
                StyleKey(AttackStyle.DEFENSIVE, 2) to 11968,
            ),
            special = SpecialAttack.Combat(
                energyCost = 50,
                accuracyMultiplier = 1.25,
                execute = { context ->  //TODO USING WHIP AS A TEST WEAPON ATM
                    context.attacker.animate(Animation(11971))
                    context.defender.gfx(Graphics(2108, 0, 100))
                    val hit = context.meleeHit()
                    if (hit[0].damage > 0) {
                        if (context.defender is Player) {
                            context.attacker.transferRunEnergy(context.defender)
                            context.defender.message("You feel drained!")
                        }
                    }
                }
            )
        ),
        MeleeWeapon(
            itemId = Weapon.itemIds("item.dragon_scimitar"),
            name = "Dragon scimitar",
            weaponStyle = WeaponStyle.SCIMITAR,
            blockAnimationId = Animation.getId("animation.scimitar_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.scimitar_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.scimitar_slash"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.scimitar_stab"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.scimitar_slash"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 55,
                accuracyMultiplier = 1.25,
                execute = { context ->
                    context.attacker.animate("animation.dragon_scimitar_special")
                    context.defender.gfx("graphic.dragon_scimitar_special", 100)
                    val hit = context.meleeHit()
                    if (hit[0].damage > 0) {
                        if (context.defender is Player) {
                            context.defender.prayer.closeProtectionPrayers()
                            context.defender.tickManager.addTicks(TickManager.Keys.DISABLED_PROTECTION_PRAYER_TICK, 8)
                        }
                    }
                }
            )
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.dragon_dagger", "item.dragon_dagger_p",
                "item.dragon_dagger_p+", "item.dragon_dagger_p++"),
            name = "Dragon dagger",
            weaponStyle = WeaponStyle.DAGGER,
            attackSpeed = 4,
            blockAnimationId = Animation.getId("animation.dragon_dagger_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.dragon_dagger_stab"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.dragon_dagger_stab"),
                StyleKey(AttackStyle.AGGRESSIVE, 2) to Animation.getId("animation.dragon_dagger_slash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.dragon_dagger_stab"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 25,
                accuracyMultiplier = 1.15,
                damageMultiplier = 1.15,
                execute = { context ->
                    context.attacker.animate("animation.dragon_dagger_special")
                    context.attacker.gfx("graphic.dragon_dagger_special", 100)
                    context.attacker.packets.sendSound(2537, 0, 1)
                    context.attacker.packets.sendSound(2537, 15, 1)
                    context.meleeHit()
                    context.meleeHit(delay = if (context.defender is NPC) 1 else 0)
                }
            )
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.dragon_mace", "item.dragon_mace"),
            name = "Dragon mace",
            weaponStyle = WeaponStyle.MACE,
            blockAnimationId = Animation.getId("animation.mace_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.mace_crush"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.mace_crush"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.mace_stab"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.mace_crush"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 25,
                accuracyMultiplier = 1.25,
                damageMultiplier = 1.50,
                execute = { context ->
                    context.attacker.animate("animation.dragon_mace_special")
                    context.attacker.gfx("graphic.dragon_mace_special", 100)
                    context.meleeHit()
                }
            )
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.granite_maul"),
            name = "Granite maul",
            weaponStyle = WeaponStyle.HAMMER,
            blockAnimationId = Animation.getId("animation.granite_maul_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.granite_maul_attack"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.granite_maul_attack"),
                StyleKey(AttackStyle.DEFENSIVE, 2) to Animation.getId("animation.granite_maul_attack"),
            ),
            special = SpecialAttack.InstantCombat(
                energyCost = 50,
                execute = { context ->
                    context.attacker.animate("animation.granite_maul_special_attack")
                    context.attacker.gfx("graphic.granite_maul_special", 100)
                    context.attacker.packets.sendSound(2541, 0, 1)
                    context.meleeHit()
                }
            )
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.torag_s_hammers", "item.torag_s_hammers_100",
                "item.torag_s_hammers_75", "item.torag_s_hammers_50",
                "item.torag_s_hammers_25", "item.torag_s_hammers_0"
            ),
            name = "Torag's hammers",
            weaponStyle = WeaponStyle.HAMMER,
            effect = SpecialEffect(
                execute = { context ->
                    context.attacker.animate(Animation("animation.torag_hammer_attack"))

                    val maxHit = CombatCalculations.calculateMeleeMaxHit(context.attacker, context.defender).maxHit
                    val maxHit1 = (maxHit + 1) / 2
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
            itemId = Item.getIds("item.chaotic_staff", "item.chaotic_staff_broken"),
            name = "Chaotic staff",
            weaponStyle = WeaponStyle.STAFF,
            blockAnimationId = Animation.getId("animation.staff_of_light_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.mace_crush"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.mace_crush"),
                StyleKey(AttackStyle.DEFENSIVE, 2) to Animation.getId("animation.mace_crush"),
            )
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.staff_of_light", "item.staff_of_light_lended",
                "item.staff_of_light_red", "item.staff_of_light_gold",
                "item.staff_of_light_blue", "item.staff_of_light_green"
            ),
            name = "Staff of light",
            weaponStyle = WeaponStyle.STAFF_OF_LIGHT,
            blockAnimationId = Animation.getId("animation.staff_of_light_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.staff_of_light_stab"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.staff_of_light_slash"),
                StyleKey(AttackStyle.DEFENSIVE, 2) to Animation.getId("animation.staff_crush"),
            ),
            special = SpecialAttack.Instant(
                energyCost = 100,
                execute = { attacker ->
                    attacker.animate("animation.staff_of_light_special")
                    attacker.gfx("graphic.staff_of_light_special")
                    attacker.tickManager.addMinutes(TickManager.Keys.STAFF_OF_LIGHT_EFFECT, 1) {
                        attacker.message("Your staff of light effect fades.")
                    }

                }
            )
        ),
        MeleeWeapon(
            itemId = listOf(3204),
            name = "Dragon halberd",
            weaponStyle = WeaponStyle.HALBERD,
            attackRange = 1,
            blockAnimationId = 430,
            animations = mapOf(
                StyleKey(AttackStyle.CONTROLLED, 0) to 437,
                StyleKey(AttackStyle.AGGRESSIVE, 1) to 440,
                StyleKey(AttackStyle.DEFENSIVE, 2) to 438,
            ),
            special = SpecialAttack.Combat(
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
            itemId = listOf(
                Rscm.lookup("item.saradomin_sword"),
                Rscm.lookup("item.saradomin_sword_2"),
                Rscm.lookup("item.lucky_saradomin_sword")
            ),
            name = "Saradomin sword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            blockAnimationId = Animation.getId("animation.two_handed_defend"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.two_handed_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.two_handed_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 2) to Animation.getId("animation.two_handed_smash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.two_handed_slash"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 100,
                damageMultiplier = 1.1,
                execute = { context ->
                    context.attacker.animate("animation.saradomin_sword_special")
                    context.attacker.gfx("graphic.saradomin_sword_special_start")
                    context.attacker.playSound(3853, 1)
                    context.hits {
                        val meleeHit = melee(delay = 0)
                        context.defender.gfx("graphic.saradomin_sword_special_end")
                        var randomHit = if (meleeHit.damage > 0) (50..150).random() else 0
                        if (context.defender is Player) {
                            if (context.defender.prayer.isMageProtecting) {
                                randomHit = 0
                            }
                        }
                        val magicHit = Hit(context.attacker, randomHit, Hit.HitLook.MAGIC_DAMAGE)
                        magicHit.critical = true
                        addHit(context.defender, magicHit)
                    }
                }
            )
        ),
        MeleeWeapon(
            itemId = listOf(
                Rscm.lookup("item.armadyl_godsword"),
                Rscm.lookup("item.armadyl_godsword_2"),
                Rscm.lookup("item.lucky_armadyl_godsword")
            ),
            name = "Armadyl godsword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            blockAnimationId = Animation.getId("animation.godsword_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.godsword_chop"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.godsword_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 2) to Animation.getId("animation.godsword_smash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.godsword_slash"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 50,
                accuracyMultiplier = 2.0,
                damageMultiplier = 1.375,
                execute = { context ->
                    context.attacker.animate("animation.armadyl_godsword_special")
                    context.attacker.gfx("graphic.armadyl_godsword_special")
                    context.meleeHit()
                }
            )
        ),
        MeleeWeapon(
            itemId = listOf(
                Rscm.lookup("item.zamorak_godsword"),
                Rscm.lookup("item.zamorak_godsword_2"),
                Rscm.lookup("item.lucky_zamorak_godsword")
            ),
            name = "Zamorak godsword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            blockAnimationId = Animation.getId("animation.godsword_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.godsword_chop"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.godsword_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 2) to Animation.getId("animation.godsword_smash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.godsword_slash"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 50,
                accuracyMultiplier = 2.0,
                damageMultiplier = 1.1,
                execute = { context ->
                    context.attacker.animate("animation.zamorak_godsword_special")
                    context.attacker.gfx("graphic.zamorak_godsword_start")
                    context.hits {
                        val hit = melee()
                        if (hit.damage > 0) {
                            context.defender.gfx("graphic.zamorak_godsword_target")
                            context.defender.tickManager.addTicks(TickManager.Keys.FREEZE_IMMUNE_TICKS, 37)
                            context.defender.tickManager.addTicks(TickManager.Keys.FREEZE_TICKS, 32) {
                                if (context.defender is Player)
                                    context.defender.message("Your staff of light effect fades.")
                                context.defender.gfx("graphic.zamorak_godsword_end")
                            }
                        }
                    }
                }
            )
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.dharok_s_greataxe","item.dharok_s_greataxe_100",
                "item.dharok_s_greataxe_75","item.dharok_s_greataxe_50",
                "item.dharok_s_greataxe_25","item.dharok_s_greataxe_0"
            ),
            name = "Dharok's greataxe",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            blockAnimationId = Animation.getId("animation.dharok_greataxe_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.dharok_greataxe_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.dharok_greataxe_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 2) to Animation.getId("animation.dharok_greataxe_smash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.dharok_greataxe_slash"),
            ),
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.bronze_scimitar", "item.iron_scimitar",
                "item.steel_scimitar", "item.black_scimitar",
                "item.mithril_scimitar", "item.adamant_scimitar",
                "item.rune_scimitar", "item.corrupt_dragon_scimitar",
                "item.c_dragon_scimitar_deg", "item.sacred_clay_scimitar"
            ),
            name = "Scimitar",
            weaponStyle = WeaponStyle.SCIMITAR,
            blockAnimationId = Animation.getId("animation.scimitar_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.scimitar_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.scimitar_slash"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.scimitar_stab"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.scimitar_slash"),
            ),
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.bronze_longsword", "item.iron_longsword", "item.steel_longsword", "item.black_longsword", "item.mithril_longsword", "item.adamant_longsword", "item.rune_longsword", "item.corrupt_dragon_longsword", "item.c_dragon_longsword_deg" ,"item.corrupt_vesta_s_longsword", "item.c_vesta_s_longsword_deg", "item.chaotic_longsword", "item.chaotic_longsword_broken"
            ),
            name = "Longsword",
            weaponStyle = WeaponStyle.SCIMITAR,
            blockAnimationId = Animation.getId("animation.chaotic_rapier_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.chaotic_slash"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.chaotic_slash"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.chaotic_stab"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.chaotic_slash"),
            ),
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.chaotic_rapier", "item.chaotic_rapier_broken", "item.brackish_blade"
            ),
            name = "Rapier",
            weaponStyle = WeaponStyle.RAPIER,
            blockAnimationId = Animation.getId("animation.chaotic_rapier_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.chaotic_stab"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.chaotic_stab"),
                StyleKey(AttackStyle.AGGRESSIVE, 2) to Animation.getId("animation.chaotic_slash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.chaotic_stab"),
            ),
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.chaotic_maul", "item.chaotic_maul_broken"
            ),
            name = "Chaotic maul",
            weaponStyle = WeaponStyle.MAUL,
            attackDelay = 1,
            blockAnimationId = Animation.getId("animation.chaotic_maul_block"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.chaotic_crush"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.chaotic_crush"),
                StyleKey(AttackStyle.DEFENSIVE, 2) to Animation.getId("animation.chaotic_crush"),
            ),
        ),
        MeleeWeapon(
            itemId = Item.getIds(
                "item.bronze_2h_sword", "item.iron_2h_sword",
                "item.steel_2h_sword", "item.black_2h_sword",
                "item.mithril_2h_sword", "item.adamant_2h_sword",
                "item.rune_2h_sword", "item.white_2h_sword"
            ),
            name = "2h sword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            blockAnimationId = Animation.getId("animation.two_handed_defend"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.two_handed_chop"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.two_handed_slash"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.two_handed_smash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.two_handed_block"),
            ),
        ),
        MeleeWeapon(
            itemId = Item.getIds("item.dragon_2h_sword", "item.lucky_dragon_2h_sword"),
            name = "Dragon 2h sword",
            weaponStyle = WeaponStyle.TWO_HANDED_SWORD,
            blockAnimationId = Animation.getId("animation.two_handed_defend"),
            animations = mapOf(
                StyleKey(AttackStyle.ACCURATE, 0) to Animation.getId("animation.two_handed_chop"),
                StyleKey(AttackStyle.AGGRESSIVE, 1) to Animation.getId("animation.two_handed_slash"),
                StyleKey(AttackStyle.CONTROLLED, 2) to Animation.getId("animation.two_handed_smash"),
                StyleKey(AttackStyle.DEFENSIVE, 3) to Animation.getId("animation.two_handed_block"),
            ),
            special = SpecialAttack.Combat(
                energyCost = 100,
                accuracyMultiplier = 1.15,
                damageMultiplier = 1.1,
                execute = { context ->
                    context.attacker.animate("animation.dragon_2h_special")
                    context.attacker.gfx("graphic.dragon_2h_special")
                    context.meleeHit()//we want to also process hit on main target
                    val targets = context.getMultiAttackTargets(1, 8)
                    for (target in targets) {
                        context.meleeHit(target = target, delay = 1)
                    }
                }
            ),
        )

    )
}
