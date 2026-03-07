package com.rs.kotlin.game.player.combat.special

enum class EffectResult {
    CONTINUE, // run normal attack
    CANCEL, // stop attack completely
    COMPLETE, // effect replaced the attack
}
