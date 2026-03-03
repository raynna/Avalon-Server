package com.rs.kotlin.game.world.util

import com.rs.java.game.World
import com.rs.java.game.player.Player

object Msg {
    // Common tags
    private const val SHAD_DARK = "<shad=000000>"
    private const val SHAD_NONE = "<shad=-1>"
    private const val END = SHAD_NONE // reset shadow

    // Palette
    const val GOLD = "ffd166"
    const val ORANGE = "ff9f1c"
    const val DARK_ORANGE = "ff6600"
    const val GREEN = "38761d"
    const val DARK_GREEN = "274e13"
    const val TEAL = "2ed1c8"
    const val BLUE = "0500ff"
    const val PURPLE = "b388ff"
    const val PINK = "ff6ea8"
    const val RED = "dc0000"
    const val GRAY = "c7c7c7"

    fun message(
        col: String,
        msg: String,
    ) = "<col=$col>$msg$END"

    fun reward(msg: String) = message(GREEN, msg)

    fun rewardRare(msg: String) = message(PURPLE, msg)

    fun chestOpen(msg: String) = message(GREEN, msg)

    fun topDamager(msg: String) = message(DARK_ORANGE, msg)

    @JvmStatic fun success(msg: String) = message(GREEN, msg)

    @JvmStatic fun info(msg: String) = message(BLUE, msg)

    @JvmStatic fun warn(msg: String) = message(RED, msg)

    @JvmStatic fun collect(msg: String) = message(RED, msg)

    fun news(msg: String) = world(DARK_ORANGE, icon = 7, msg = "News: $msg")

    fun newsRare(msg: String) = world(RED, icon = 7, msg = "News: $msg")

    fun newsEpic(msg: String) = world(PURPLE, icon = 7, msg = "News: $msg")

    @JvmStatic fun success(
        player: Player,
        msg: String,
    ) = player.message(message(GREEN, msg))

    @JvmStatic fun info(
        player: Player,
        msg: String,
    ) = player.message(message(BLUE, msg))

    @JvmStatic fun warn(
        player: Player,
        msg: String,
    ) = player.message(message(RED, msg))

    @JvmStatic fun collect(
        player: Player,
        msg: String,
    ) = player.message(message(RED, msg))

    @JvmStatic fun world(
        col: String,
        msg: String,
    ) = World.sendWorldMessage("<img=7>$SHAD_DARK<col=$col>$msg$END", false)

    @JvmStatic fun world(
        col: String,
        icon: Int? = 7,
        msg: String,
    ) = World.sendWorldMessage("<img=$icon>$SHAD_DARK<col=$col>$msg$END", false)

    @JvmStatic fun reward(
        player: Player,
        msg: String,
    ) = player.message(reward(msg))

    @JvmStatic fun rewardRare(
        player: Player,
        msg: String,
    ) = player.message(rewardRare(msg))
}
