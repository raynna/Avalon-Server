package com.rs.kotlin.game.npc.combatdata

class Immunities(
    val poison: Boolean,
    val venom: Boolean,
    val cannons: Boolean,
    val thralls: Boolean,
    val burn: Boolean
) {
    @JvmName("isPoisonImmune")
    fun getPoison() = poison
    @JvmName("isVenomImmune")
    fun getVenom() = venom
    @JvmName("isCannonImmune")
    fun getCannons() = cannons
    @JvmName("isThrallsImmune")
    fun getThralls() = thralls
    @JvmName("isBurnImmune")
    fun getBurn() = burn
}
