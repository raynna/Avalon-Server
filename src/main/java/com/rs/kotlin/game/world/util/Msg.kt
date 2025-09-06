package com.rs.kotlin.game.world.util

object Msg {
    // Common tags
    private const val SHAD_DARK = "<shad=000000>"
    private const val SHAD_NONE = "<shad=-1>"
    private const val END = SHAD_NONE // reset shadow

    // Palette (WCAG-ish contrast against most RS backdrops)
    const val GOLD     = "ffd166"
    const val ORANGE   = "ff9f1c"
    const val GREEN    = "4cd137"
    const val TEAL     = "2ed1c8"
    const val BLUE     = "63c5ff"
    const val PURPLE   = "b388ff"
    const val PINK     = "ff6ea8"
    const val RED      = "ff4d4d"
    const val GRAY     = "c7c7c7"

    // Builders
    fun player(col: String, msg: String) = "$SHAD_DARK<col=$col>$msg$END"
    fun world(col: String, msg: String)  = "<img=7>$SHAD_DARK<col=$col>$msg$END"

    // Specific styles
    fun reward(msg: String)        = player(GREEN, msg)
    fun rewardRare(msg: String)    = player(PURPLE, msg)
    fun chestOpen(msg: String)     = player(TEAL, msg)
    fun topDamager(msg: String)    = player(ORANGE, msg)
    fun info(msg: String)          = player(BLUE, msg)
    fun warn(msg: String)          = player(RED, msg)

    fun news(msg: String)          = world(ORANGE, "News: $msg")
    fun newsRare(msg: String)      = world(RED,    "News: $msg")
    fun newsEpic(msg: String)      = world(PURPLE, "News: $msg")
}
