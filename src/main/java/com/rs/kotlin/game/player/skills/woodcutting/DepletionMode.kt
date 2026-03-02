package com.rs.kotlin.game.player.skills.woodcutting

enum class DepletionMode {
    INSTANT, // disappears on first success
    TIMED, // uses despawnTicks timer
}
