package com.rs.kotlin.game.player.dialogue

import com.rs.java.game.player.Player
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class DialogueManager(
    private val player: Player,
) {
    var continueContinuation: Continuation<Unit>? = null
    var optionContinuation: Continuation<Int>? = null

    fun handleContinue() {
        val cont = continueContinuation ?: return
        continueContinuation = null
        cont.resume(Unit)
    }

    fun handleOption(option: Int) {
        val cont = optionContinuation ?: return
        optionContinuation = null
        cont.resume(option)
    }

    fun close() {
        val cont = continueContinuation
        val opt = optionContinuation

        continueContinuation = null
        optionContinuation = null

        cont?.resume(Unit)
        opt?.resume(-1)

        player.interfaceManager.closeChatBoxInterface()
    }
}
