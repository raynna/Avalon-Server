package raynna.game.player.combat.damage

import raynna.game.Entity
import raynna.game.Hit
import raynna.game.npc.NPC
import raynna.game.player.Player
import raynna.game.player.bot.PlayerBotManager
import raynna.util.Utils
import raynna.game.player.combat.CombatCalculations
import raynna.game.player.combat.CombatType
import raynna.game.player.combat.special.CombatContext
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min

enum class CombatHitRoll {
    GUARANTEED,
    NORMAL,
}

class HitBuilder(
    private val context: CombatContext,
    private val type: CombatType,
    private val target: Entity,
) {
    private var delayTicks: Int = 0

    private var accuracyMultiplier: Double = 1.0
    private var specialDamageMultiplier: Double = 1.0

    private var forcedMinDamage: Int? = null
    private var forcedMaxDamage: Int? = null
    private var maxHitOverride: Int? = null
    private var damageRangeFromMaxHit: ((Int) -> IntRange)? = null

    private var forcedHitLook: Hit.HitLook? = null
    private var spellId: Int = -1
    private var baseDamage: Int = -1

    private var postRollBonus: ((Hit) -> Unit)? = null

    fun delay(ticks: Int) = apply { delayTicks = ticks }

    fun accuracy(mult: Double) = apply { accuracyMultiplier = mult }

    fun damageMultiplier(mult: Double) = apply { specialDamageMultiplier = mult }

    fun damage(
        min: Int,
        max: Int,
    ) = apply {
        forcedMinDamage = min
        forcedMaxDamage = max
    }

    /** Force damage roll 0..amount */
    fun damage(amount: Int) =
        apply {
            forcedMinDamage = 0
            forcedMaxDamage = amount
        }

    /** Force a specific hit look (optional) */
    fun look(look: Hit.HitLook) = apply { forcedHitLook = look }

    /** Magic-only: select spell */
    fun spell(id: Int) = apply { spellId = id }

    fun damageFromMaxHit(range: (maxHit: Int) -> IntRange) =
        apply {
            damageRangeFromMaxHit = range
        }

    fun bonus(block: (Hit) -> Unit) =
        apply {
            postRollBonus = block
        }

    fun maxHit(max: Int) =
        apply {
            maxHitOverride = max
        }

    fun baseDamage(amount: Int) =
        apply {
            baseDamage = amount
        }

    private var damage: Int = 0
    private var look: Hit.HitLook = Hit.HitLook.REGULAR_DAMAGE
    private var source: Entity = context.attacker

    fun apply(): Hit {
        val hit = Hit(source, damage, look)
        if (source is Player &&
            target is Player &&
            !PlayerBotManager.isManagedBot(source as Player) &&
            PlayerBotManager.isManagedBot(target as Player)
        ) {
            val attacker = source as Player
            val defender = target as Player
            println(
                "[CombatBot QUEUE] attacker=${attacker.displayName} defender=${defender.displayName} " +
                    "type=manual look=${hit.look} damage=${hit.damage} delay=${delayTicks}"
            )
        }
        context.combat.delayHits(PendingHit(hit, target, delayTicks))
        return hit
    }

    private fun rollDamage(
        maxHit: Int,
        minHit: Int = 0,
    ): Int {
        if (maxHit <= 0) return 0
        return if (minHit >= maxHit) minHit else Utils.random(minHit, maxHit)
    }

    fun roll(mode: CombatHitRoll = CombatHitRoll.NORMAL): Hit {
        val attacker = context.attacker
        require(attacker is Player) { "HitBuilder currently expects attacker to be a Player" }

        val accurate =
            when (mode) {
                CombatHitRoll.GUARANTEED -> {
                    true
                }

                CombatHitRoll.NORMAL -> {
                    ThreadLocalRandom.current().nextDouble() <
                        CombatCalculations.getHitChance(attacker, target, type, accuracyMultiplier)
                }
            }
        val maxHit: MaxHit =
            when (type) {
                CombatType.MELEE -> {
                    CombatCalculations.getMeleeMaxHit(attacker, target, specialDamageMultiplier)
                }

                CombatType.RANGED -> {
                    CombatCalculations.getRangedMaxHit(attacker, target, specialDamageMultiplier)
                }

                CombatType.MAGIC -> {
                    CombatCalculations.getMagicMaxHit(
                        attacker,
                        target,
                        baseDamage = baseDamage,
                        spellId = spellId,
                        specialMultiplier = specialDamageMultiplier,
                    )
                }
            }

        val finalMaxHit =
            maxHitOverride?.let { cap -> minOf(cap, maxHit.max) } ?: maxHit.max

        val look =
            forcedHitLook ?: when (type) {
                CombatType.MELEE -> Hit.HitLook.MELEE_DAMAGE
                CombatType.RANGED -> Hit.HitLook.RANGE_DAMAGE
                CombatType.MAGIC -> Hit.HitLook.MAGIC_DAMAGE
            }

        val hit = Hit(attacker, 0, finalMaxHit, look)
        hit.baseMaxHit = maxHit.base
        hit.maxHit = finalMaxHit

        if (!accurate) {
            hit.damage = 0
            if (target is Player &&
                !PlayerBotManager.isManagedBot(attacker) &&
                PlayerBotManager.isManagedBot(target)
            ) {
                val defender = target as Player
                println(
                    "[CombatBot QUEUE] attacker=${attacker.displayName} defender=${defender.displayName} " +
                        "type=$type look=${hit.look} damage=${hit.damage} max=${hit.maxHit} " +
                        "accurate=false delay=${delayTicks}"
                )
            }
            context.combat.delayHits(PendingHit(hit, target, delayTicks))
            return hit
        }

        hit.damage =
            when {
                forcedMinDamage != null && forcedMaxDamage != null -> {
                    Utils.random(forcedMinDamage!!, forcedMaxDamage!!)
                }

                damageRangeFromMaxHit != null -> {
                    val range = damageRangeFromMaxHit!!(finalMaxHit)
                    if (range.first >= range.last) {
                        range.first
                    } else {
                        Utils.random(range.first, range.last)
                    }
                }

                else -> {
                    rollDamage(finalMaxHit, maxHit.min)
                }
            }
        if (target is NPC && target.id == 4474) {
            hit.damage =
                if (damageRangeFromMaxHit != null) {
                    damageRangeFromMaxHit!!(hit.maxHit).last
                } else if (forcedMaxDamage != null) {
                    forcedMaxDamage!!
                } else {
                    hit.maxHit
                }
        }

        val critThreshold = (maxHit.base * 0.99).toInt()
        if (hit.damage >= critThreshold) {
            hit.setCriticalMark()
            hit.critical = true
        }

        postRollBonus?.invoke(hit)
        if (target is Player &&
            !PlayerBotManager.isManagedBot(attacker) &&
            PlayerBotManager.isManagedBot(target)
        ) {
            val defender = target as Player
            println(
                "[CombatBot QUEUE] attacker=${attacker.displayName} defender=${defender.displayName} " +
                    "type=$type look=${hit.look} damage=${hit.damage} max=${hit.maxHit} " +
                    "baseMax=${hit.baseMaxHit} accurate=true delay=${delayTicks}"
            )
        }
        context.combat.delayHits(PendingHit(hit, target, delayTicks))
        return hit
    }
}
