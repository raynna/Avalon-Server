// app/tabs/TabChrome.kt
package com.rs.kotlin.game.player.customtab

import com.rs.java.game.player.Player

object TabChrome {

    private const val IFACE = 3002
    private const val FIRST_SLOT = 3
    private const val LAST_SLOT = 22

    // star component IDs
    private const val BLUE_STAR = 62
    private const val GREEN_STAR = 61
    private const val RED_STAR = 60
    private const val PURPLE_STAR = 59
    private const val YELLOW_STAR = 26

    // highlight sprites
    private const val BLUE_HIGHLIGHTED = 12184
    private const val GREEN_HIGHLIGHTED = 12182
    private const val RED_HIGHLIGHTED = 12186
    private const val PURPLE_HIGHLIGHTED = 12185
    private const val YELLOW_HIGHLIGHTED = 12187

    fun init(player: Player) {
        val pk = player.packets

        // ensure all star icons are visible + correct sprites
        pk.sendHideIComponent(IFACE, BLUE_STAR, false)
        pk.sendHideIComponent(IFACE, GREEN_STAR, false)
        pk.sendHideIComponent(IFACE, RED_STAR, false)
        pk.sendHideIComponent(IFACE, PURPLE_STAR, false)
        pk.sendHideIComponent(IFACE, YELLOW_STAR, false)

        pk.sendSpriteOnIComponent(IFACE, BLUE_STAR, BLUE_HIGHLIGHTED)
        pk.sendSpriteOnIComponent(IFACE, GREEN_STAR, GREEN_HIGHLIGHTED)
        pk.sendSpriteOnIComponent(IFACE, RED_STAR, RED_HIGHLIGHTED)
        pk.sendSpriteOnIComponent(IFACE, PURPLE_STAR, PURPLE_HIGHLIGHTED)
        pk.sendSpriteOnIComponent(IFACE, YELLOW_STAR, YELLOW_HIGHLIGHTED)

        // hide the title by default
        pk.sendHideIComponent(IFACE, 25, true)

        // hide all rows
        hideAll(player)
    }

    fun hideAll(player: Player) {
        val pk = player.packets
        for (i in FIRST_SLOT..LAST_SLOT) {
            pk.sendHideIComponent(IFACE, i, true)
        }
        for (i in 28..56) {
            pk.sendHideIComponent(IFACE, i, true)
        }
    }
}
