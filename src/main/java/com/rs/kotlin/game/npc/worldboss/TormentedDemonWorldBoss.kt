package com.rs.kotlin.game.npc.worldboss

import com.rs.java.game.WorldTile
import com.rs.java.game.Hit
import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.utils.Utils
import com.rs.java.game.World
import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.player.Player
import com.rs.java.game.player.Equipment

class TormentedDemonWorldBoss(
    tile: WorldTile,
    idleTimeoutMs: Long,
    gracePeriodMs: Long,
    handler: RandomWorldBossHandler,
) : WorldBossNPC(8349, tile, idleTimeoutMs, gracePeriodMs, handler) {

    private val MAX_PRAYERS = 3
    private val MELEE = 0
    private val MAGIC = 1
    private val RANGE = 2
    
    private val SHIELD_DAMAGE_REDUCTION = 0.25
    private val SHIELD_COOLDOWN = 200
    private val DAMAGE_THRESHOLD = 310
    private val MIN_DAMAGE_ON_MISS = 20
    
    private val MAX_ATTACK_TICKS = 26
    private val ATTACK_STYLE_CHANGE_DELAY = 6
    
    private val RANDOM_PROJECTILE_ANIM = Animation(10918)
    private val SHIELD_GFX = Graphics(1885)
    private val SPLASH_GFX = Graphics(1883)
    
    private val RANDOM_PROJECTILE_ID = 1884
    private val RANDOM_PROJECTILE_RADIUS = 3
    private val RANDOM_PROJECTILE_RANGE = 7

    private var demonPrayer = BooleanArray(MAX_PRAYERS)
    private var cachedDamage = IntArray(MAX_PRAYERS)
    private var shieldTimer = 0
    var currentCombatType = 0
    var previousCombatType = -1
    private var attackTicks = 0
    private var currentType = 0

    init {
        // Initialize like TormentedDemon
        setForceTargetDistance(64)
        shieldTimer = 0
        switchPrayers(Utils.random(1, 2))
    }

    fun switchPrayers(type: Int) {
        transformIntoNPC(8349 + type)
        demonPrayer[type] = true
    }

    override fun processEntity() {
        super.processEntity()
        if (isDead) return

        // Process Tormented Demon specific logic
        if (combat != null && combat.process()) {
            incrementAttackTicks()
            handleAttackStyleChange()
            decrementShieldTimer()
            checkDamageThreshold()
        }
    }

    private fun incrementAttackTicks() {
        if (attackTicks < MAX_ATTACK_TICKS) attackTicks++
    }

    private fun handleAttackStyleChange() {
        if (attackTicks < MAX_ATTACK_TICKS) return

        resetAttackTicks()

        var attackType = Utils.getRandom(2)
        while (attackType == currentCombatType) {
            attackType = Utils.getRandom(2)
        }

        sendRandomProjectile()
        previousCombatType = currentCombatType
        currentCombatType = attackType
        combat?.setAttackDelay(ATTACK_STYLE_CHANGE_DELAY)
    }

    private fun decrementShieldTimer() {
        if (shieldTimer > 0) shieldTimer--
    }

    private fun checkDamageThreshold() {
        if (cachedDamage[currentType] >= DAMAGE_THRESHOLD) {
            demonPrayer = BooleanArray(MAX_PRAYERS)
            switchPrayers(currentType)
            cachedDamage = IntArray(MAX_PRAYERS)
        }
    }

    override fun handleIncommingHit(hit: Hit) {
        super.handleIncommingHit(hit)
        if (shieldTimer <= 0)
            gfx(SHIELD_GFX)
        if (shieldTimer <= 0 && hit.damage > 0) {
            hit.damage = (hit.damage * SHIELD_DAMAGE_REDUCTION).toInt()
        }

        when (hit.look) {
            Hit.HitLook.MELEE_DAMAGE -> handleMeleeHit(hit)
            Hit.HitLook.MAGIC_DAMAGE -> handleMagicHit(hit)
            Hit.HitLook.RANGE_DAMAGE, Hit.HitLook.CANNON_DAMAGE -> handleRangedHit(hit)
            else -> {}
        }

        if (hit.damage <= 0) {
            cachedDamage[currentType] += MIN_DAMAGE_ON_MISS
        }

        val source = hit.source
        if (source is Player) {
            if (shieldTimer <= 0 && hit.damage > 0) {
                source.packets.sendGameMessage("The demon shield absorbs most of your damage.")
            }
        }
    }

    private fun handleMeleeHit(hit: Hit) {
        currentType = MELEE
        val source = hit.source
        if (source is Player) {
            if (demonPrayer[currentType] && source.temporaryAttributtes.get("VERAC_EFFECT") != true) {
                hit.damage = 0
            } else {
                val weapon = source.equipment.getItem(Equipment.SLOT_WEAPON.toInt())
                if ((weapon.name.contains("darklight", ignoreCase = true) || 
                     weapon.name.contains("silverlight", ignoreCase = true)) && hit.damage > 0) {
                    shieldTimer = SHIELD_COOLDOWN
                    source.packets.sendGameMessage("The demon is temporarily weakened by your weapon.")
                }
                cachedDamage[currentType] += hit.damage
            }
        }
    }

    private fun handleMagicHit(hit: Hit) {
        currentType = MAGIC
        if (demonPrayer[currentType]) hit.damage = 0
        else cachedDamage[currentType] += hit.damage
    }

    private fun handleRangedHit(hit: Hit) {
        currentType = RANGE
        if (demonPrayer[currentType]) hit.damage = 0
        else cachedDamage[currentType] += hit.damage
    }

    fun sendRandomProjectile() {
        val tile = WorldTile(
            x + Utils.random(-RANDOM_PROJECTILE_RANGE, RANDOM_PROJECTILE_RANGE),
            y + Utils.random(-RANDOM_PROJECTILE_RANGE, RANDOM_PROJECTILE_RANGE),
            plane
        )
        animate(RANDOM_PROJECTILE_ANIM)
        World.sendGroundProjectile(this, tile, RANDOM_PROJECTILE_ID)

        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                for (regionId in mapRegionsIds) {
                    val playerIndexes = World.getRegion(regionId).playerIndexes ?: continue

                    for (playerIndex in playerIndexes) {
                        val player = World.getPlayers()[playerIndex] ?: continue
                        if (player.isDead || player.hasFinished() || !player.hasStarted() ||
                            Utils.getDistance(player, tile) > RANDOM_PROJECTILE_RADIUS) {
                            continue
                        }

                        val attackedByMap = player.skullList as? Map<*, *>
                        if (attackedByMap == null || !attackedByMap.containsKey(this@TormentedDemonWorldBoss)) {
                            continue
                        }

                        player.gfx(SPLASH_GFX)
                        player.message("The demon's magical attack splashes on you.")
                        player.applyHit(Hit(this@TormentedDemonWorldBoss, Utils.random(138, 289), Hit.HitLook.MAGIC_DAMAGE, 0))
                    }
                }
            }
        }, 2)
    }

    // Getters for combat system
    fun getAttackTicks(): Int = attackTicks
    fun resetAttackTicks() { attackTicks = 0 }
}