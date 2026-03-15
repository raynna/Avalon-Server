package com.rs.kotlin.game.player.queue

import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.dialogue.DialogueRenderer
import com.rs.kotlin.game.player.dialogue.FacialExpression
import kotlin.coroutines.suspendCoroutine

class QueueTask(
    val player: Player,
) {
    private val renderer = DialogueRenderer(player)

    suspend fun chatNpc(
        text: String,
        npcId: Int,
        facialExpression: FacialExpression = FacialExpression.NORMAL,
    ) {
        renderer.npc(npcId, facialExpression.id, text)
        waitContinue()
    }

    suspend fun chatPlayer(
        text: String,
        facialExpression: FacialExpression = FacialExpression.NORMAL,
    ) {
        renderer.player(facialExpression.id, text)
        waitContinue()
    }

    suspend fun item(
        itemId: Int,
        text: String,
    ) {
        renderer.item(itemId, 1, text)
        waitContinue()
    }

    suspend fun options(vararg options: String): Int {
        renderer.options("Choose an option.", *options)

        return suspendCoroutine { cont ->
            player.newDialogueManager.optionContinuation = cont
        }
    }

    suspend fun waitContinue() {
        suspendCoroutine<Unit> { cont ->
            player.newDialogueManager.continueContinuation = cont
        }
    }
}
