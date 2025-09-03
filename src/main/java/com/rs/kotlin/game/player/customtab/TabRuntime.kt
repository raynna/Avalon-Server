// core/runtime/TabRuntime.kt
package com.rs.kotlin.game.player.customtab

import com.rs.java.game.player.Player

object TabRuntime {

    /**
     * Renders a page:
     * - hides all slots, then assigns component IDs to items sequentially
     * - writes text for visible items
     * - configures chrome (stars, back/forward, title)
     */
    fun open(player: Player, page: TabPage) {
        // 1. Reset chrome baseline (stars, hides, title)
        TabChrome.init(player)

        // 2. Tab-specific pre-setup
        page.beforeOpen?.invoke(player)

        // 3. Render DSL rows (labels, actions, spacers…)
        page.render(player)
    }

    fun handleButton(player: Player, page: TabPage, compId: Int): Boolean {
        // 1. Direct extras (raw component IDs like stars, Yes/No, etc.)
        page.extraHandlers[compId]?.let { it(player); return true }

        // 2. Auto-mapped items (Label/Action assigned via render)
        val item = page.idMap.entries.find { it.value == compId }?.key
        if (item is Action) {
            item.click(player)
            return true
        }

        // 3. Back/forward chrome
        if (compId == page.chrome.backButton) {
            page.onBack?.invoke(player); return true
        }
        if (compId == page.chrome.forwardButton) {
            page.onForward?.invoke(player); return true
        }

        return false
    }


    private fun showChrome(player: Player, page: TabPage) {
        val pk = player.packets
        val iface = page.chrome.ifaceId

        // Show stars and set sprites (example mirrors your original)
        fun show(id: Int) = pk.sendHideIComponent(iface, id, false)
        show(page.chrome.blueStar)
        show(page.chrome.greenStar)
        show(page.chrome.redStar)
        show(page.chrome.purpleStar)
        show(page.chrome.yellowStar)

        pk.sendSpriteOnIComponent(iface, page.chrome.blueStar, page.chrome.blueHighlighted)
        pk.sendSpriteOnIComponent(iface, page.chrome.greenStar, page.chrome.greenHighlighted)
        pk.sendSpriteOnIComponent(iface, page.chrome.redStar, page.chrome.redHighlighted)
        pk.sendSpriteOnIComponent(iface, page.chrome.purpleStar, page.chrome.purpleHighlighted)
        pk.sendSpriteOnIComponent(iface, page.chrome.yellowStar, page.chrome.yellowHighlighted)

        // Optional title
        val titleId = 25 // reuse from your Java
        if (page.title == null) {
            pk.sendHideIComponent(iface, titleId, true)
        } else {
            pk.sendHideIComponent(iface, titleId, false)
            pk.sendTextOnComponent(iface, titleId, page.title)
        }
    }
}
