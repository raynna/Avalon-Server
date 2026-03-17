package com.rs.kotlin.game.player.queue

import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.dialogue.DialogueRenderer
import com.rs.kotlin.game.player.dialogue.DialogueStoppedException
import com.rs.kotlin.game.player.dialogue.FacialExpression
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

class QueueTask(
    val player: Player,
) {
    private val renderer = DialogueRenderer(player)

    suspend fun npc(
        text: String,
        npcId: Int,
        facialExpression: FacialExpression = FacialExpression.HAPPY,
    ) {
        renderer.npc(npcId, facialExpression.id, text)
        waitContinue()
    }

    suspend fun player(
        text: String,
        facialExpression: FacialExpression = FacialExpression.HAPPY,
    ) {
        renderer.player(facialExpression.id, text)
        waitContinue()
    }

    suspend fun simple(text: String) {
        renderer.simple(text)
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

        return suspendCancellableCoroutine { cont ->
            player.newDialogueManager.optionContinuation = cont
        }
    }

    // ---------- NO CONTINUE ----------

    suspend fun npcNoContinue(
        npcId: Int,
        text: String,
        facialExpression: FacialExpression = FacialExpression.HAPPY,
        delayMs: Long = 1200,
    ) {
        renderer.npcNoContinue(npcId, facialExpression.id, text)
        delay(delayMs)
    }

    suspend fun playerNoContinue(
        text: String,
        facialExpression: FacialExpression = FacialExpression.HAPPY,
        delayMs: Long = 1200,
    ) {
        renderer.playerNoContinue(facialExpression.id, text)
        delay(delayMs)
    }

    suspend fun itemNoContinue(
        itemId: Int,
        text: String,
        delayMs: Long = 1200,
    ) {
        renderer.itemNoContinue(itemId, 1, text)
        delay(delayMs)
    }

    suspend fun noContinue(
        text: String,
        delayMs: Long = 1200,
    ) {
        renderer.simpleNoContinue(text)
        delay(delayMs)
    }

    suspend fun npcPlayer(
        npcId: Int,
        vararg text: String,
        leftExpression: FacialExpression = FacialExpression.HAPPY,
        rightExpression: FacialExpression = FacialExpression.HAPPY,
    ) {
        renderer.doubleEntity(
            leftIsPlayer = false,
            leftId = npcId,
            leftAnimation = leftExpression.id,
            rightIsPlayer = true,
            rightId = player.index,
            rightAnimation = rightExpression.id,
            *text,
        )
        waitContinue()
    }

    suspend fun playerNpc(
        npcId: Int,
        vararg text: String,
        leftExpression: FacialExpression = FacialExpression.HAPPY,
        rightExpression: FacialExpression = FacialExpression.HAPPY,
    ) {
        renderer.doubleEntity(
            leftIsPlayer = true,
            leftId = player.index,
            leftAnimation = leftExpression.id,
            rightIsPlayer = false,
            rightId = npcId,
            rightAnimation = rightExpression.id,
            *text,
        )
        waitContinue()
    }

    suspend fun doubleItemSelection(
        item1Id: Int,
        item1Amount: Int,
        item2Id: Int,
        item2Amount: Int,
        option1: String,
        option2: String,
    ): Int {
        renderer.doubleItemSelection(
            item1Id,
            item1Amount,
            item2Id,
            item2Amount,
            option1,
            option2,
        )

        return suspendCancellableCoroutine { cont ->
            player.newDialogueManager.optionContinuation = cont
        }
    }

    suspend fun pause(ms: Long) {
        delay(ms)
    }

    suspend fun stop() {
        player.newDialogueManager.close()
        throw CancellationException()
    }

    suspend fun selection(
        title: String,
        itemIds: IntArray,
        names: List<String>,
    ): Int {
        renderer.selection(title, itemIds, names)

        return suspendCancellableCoroutine { cont ->
            player.newDialogueManager.optionContinuation = cont
        }
    }

    suspend fun waitContinue() {
        suspendCancellableCoroutine { cont ->
            player.newDialogueManager.continueContinuation = cont
        }
    }
}
