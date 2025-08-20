package com.rs.kotlin.game.npc.combatdata

data class MagicDefence(val magic: Int) {
    @JvmName("getMagicBonus")
    fun getMagicBonus() = magic
}
