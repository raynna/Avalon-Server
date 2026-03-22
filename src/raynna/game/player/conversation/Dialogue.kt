package raynna.game.player.conversation

import raynna.core.cache.defintions.NPCDefinitions
import raynna.game.player.Player

class Dialogue(
    val player: Player,
) {
    fun npc(
        npcId: Int,
        animation: Int = FacialExpression.NORMAL.id,
        vararg text: String,
    ) {
        val name = NPCDefinitions.getNPCDefinitions(npcId).name

        player.interfaceManager.sendChatBoxInterface(1184)
        player.packets.sendTextOnComponent(1184, 17, name)
        player.packets.sendTextOnComponent(1184, 13, text.joinToString(" "))
        player.packets.sendNPCOnIComponent(1184, 11, npcId)

        if (animation != -1) {
            player.packets.sendIComponentAnimation(animation, 1184, 11)
        }
    }

    fun player(
        animation: Int = FacialExpression.NORMAL.id,
        vararg text: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1191)

        player.packets.sendTextOnComponent(1191, 8, player.displayName)
        player.packets.sendTextOnComponent(1191, 17, text.joinToString(" "))
        player.packets.sendPlayerOnIComponent(1191, 15)

        if (animation != -1) {
            player.packets.sendIComponentAnimation(animation, 1191, 15)
        }
    }

    fun item(
        itemId: Int,
        vararg text: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1189)

        player.packets.sendItemOnIComponent(1189, 1, itemId, 1)
        player.packets.sendTextOnComponent(1189, 4, text.joinToString(" "))
    }

    fun options(
        title: String = "Choose an option.",
        vararg options: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1188)

        val params = mutableListOf<Any>()
        params.add(options.size)

        options.reversed().forEach { params.add(it) }

        player.packets.sendTextOnComponent(1188, 20, title)
        player.packets.sendRunScript(5589, params.toTypedArray())
    }

    fun simple(vararg text: String) {
        player.interfaceManager.sendChatBoxInterface(1186)

        val formatted = text.joinToString("<br>") { it }

        player.packets.sendTextOnComponent(
            1186,
            1,
            "<p=1>$formatted",
        )
    }

    fun close() {
        player.interfaceManager.closeChatBoxInterface()
    }
}
