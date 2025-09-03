package com.rs.kotlin.game.player.customtab

import com.rs.java.game.player.Player

fun TabPage.render(player: Player) {
    val pk = player.packets
    val iface = chrome.ifaceId
    var compId = chrome.firstSlot

    idMap.clear()

    for (item in items) {
        if (compId > chrome.lastSlot) break

        when (item) {
            is Section -> {
                pk.sendHideIComponent(iface, compId, false)
                pk.sendTextOnComponent(iface, compId, item.text(player) ?: "")
                idMap[item] = compId
            }
            is Label -> {
                pk.sendHideIComponent(iface, compId, false)
                pk.sendTextOnComponent(iface, compId, item.text(player) ?: "")
                idMap[item] = compId
            }
            is Action -> {
                pk.sendHideIComponent(iface, compId, false)
                pk.sendTextOnComponent(iface, compId, item.text(player) ?: "")
                idMap[item] = compId
                extraHandlers[compId] = { p -> item.click(p) }
            }
            is Spacer -> {
                // Leave hidden or blank
                pk.sendHideIComponent(iface, compId, true)
            }
        }

        compId++
    }

    // Title
    if (title != null) {
        pk.sendHideIComponent(iface, 25, false)
        pk.sendTextOnComponent(iface, 25, title)
    } else {
        pk.sendHideIComponent(iface, 25, true)
    }

    // Back/forward buttons
    chrome.backButton.let { backId ->
        pk.sendHideIComponent(iface, backId, onBack == null)
    }
    chrome.forwardButton.let { fwdId ->
        pk.sendHideIComponent(iface, fwdId, onForward == null)
    }
}
