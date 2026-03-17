package com.rs.kotlin.game.player.dialogue

import com.rs.core.cache.defintions.NPCDefinitions
import com.rs.java.game.player.Player
import com.rs.java.game.player.content.SkillsDialogue

class DialogueRenderer(
    private val player: Player,
) {
    fun npc(
        npcId: Int,
        animation: Int,
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
        animation: Int,
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
        amount: Int = 1,
        vararg text: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1189)

        player.packets.sendItemOnIComponent(1189, 1, itemId, amount)
        player.packets.sendTextOnComponent(1189, 4, text.joinToString(" "))
    }

    fun options(
        title: String,
        vararg options: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1188)

        val params = mutableListOf<Any>()
        params.add(options.size)

        options.reversed().forEach { params.add(it) }

        player.packets.sendTextOnComponent(1188, 20, title)
        player.packets.sendRunScript(5589, *params.toTypedArray())
    }

    fun simple(vararg text: String) {
        player.interfaceManager.sendChatBoxInterface(1186)

        val formatted = text.joinToString("<br>")

        player.packets.sendTextOnComponent(
            1186,
            1,
            "<p=1>$formatted",
        )
    }

    fun npcNoContinue(
        npcId: Int,
        animation: Int,
        vararg text: String,
    ) {
        val name = NPCDefinitions.getNPCDefinitions(npcId).name

        player.interfaceManager.sendChatBoxInterface(1192)
        player.packets.sendTextOnComponent(1192, 16, name)
        player.packets.sendTextOnComponent(1192, 12, text.joinToString(" "))
        player.packets.sendNPCOnIComponent(1192, 11, npcId)

        if (animation != -1) {
            player.packets.sendIComponentAnimation(animation, 1192, 11)
        }
    }

    fun playerNoContinue(
        animation: Int,
        vararg text: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1192)

        player.packets.sendTextOnComponent(1192, 16, player.displayName)
        player.packets.sendTextOnComponent(1192, 12, text.joinToString(" "))
        player.packets.sendPlayerOnIComponent(1192, 11)

        if (animation != -1) {
            player.packets.sendIComponentAnimation(animation, 1192, 11)
        }
    }

    fun item(
        animation: Int,
        vararg text: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1187)

        player.packets.sendTextOnComponent(1191, 8, player.displayName)
        player.packets.sendTextOnComponent(1191, 17, text.joinToString(" "))
        player.packets.sendPlayerOnIComponent(1191, 15)

        if (animation != -1) {
            player.packets.sendIComponentAnimation(animation, 1191, 15)
        }
    }

    fun itemNoContinue(
        itemId: Int,
        amount: Int = 1,
        vararg text: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1190)

        player.packets.sendItemOnIComponent(1190, 1, itemId, amount)
        player.packets.sendTextOnComponent(1190, 4, text.joinToString(" "))
    }

    fun simpleNoContinue(vararg text: String) {
        val interfaceId =
            when (text.size) {
                1 -> 215
                2 -> 216
                3 -> 217
                4 -> 218
                else -> 219
            }

        player.interfaceManager.sendChatBoxInterface(interfaceId)

        text.forEachIndexed { index, line ->
            player.packets.sendTextOnComponent(interfaceId, 3 + index, line)
        }
    }

    fun doubleEntity(
        leftIsPlayer: Boolean,
        leftId: Int,
        leftAnimation: Int,
        rightIsPlayer: Boolean,
        rightId: Int,
        rightAnimation: Int,
        vararg text: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1187)

        val formatted = text.joinToString("<br>")
        player.packets.sendTextOnComponent(1187, 1, "<p=1>$formatted")

        if (leftIsPlayer) {
            player.packets.sendPlayerOnIComponent(1187, 8)
        } else {
            player.packets.sendNPCOnIComponent(1187, 8, leftId)
        }

        if (leftAnimation != -1) {
            player.packets.sendIComponentAnimation(leftAnimation, 1187, 8)
        }

        if (rightIsPlayer) {
            player.packets.sendPlayerOnIComponent(1187, 9)
        } else {
            player.packets.sendNPCOnIComponent(1187, 9, rightId)
        }

        if (rightAnimation != -1) {
            player.packets.sendIComponentAnimation(rightAnimation, 1187, 9)
        }
    }

    fun doubleItemSelection(
        item1Id: Int,
        item1Amount: Int,
        item2Id: Int,
        item2Amount: Int,
        option1: String,
        option2: String,
    ) {
        player.interfaceManager.sendChatBoxInterface(1185)

        player.packets.sendItemOnIComponent(1185, 4, item1Id, item1Amount)
        player.packets.sendItemOnIComponent(1185, 5, item2Id, item2Amount)

        player.packets.sendTextOnComponent(1185, 20, option1)
        player.packets.sendTextOnComponent(1185, 25, option2)
    }

    fun selection(
        title: String,
        itemIds: IntArray,
        names: List<String>,
    ) {
        var count = 0

        SkillsDialogue.sendStoreDialogue(
            player,
            title,
            itemIds,
        ) { names[count++] }
    }

    fun close() {
        player.interfaceManager.closeChatBoxInterface()
    }
}
