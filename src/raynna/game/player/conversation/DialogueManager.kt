package raynna.game.player.conversation

import raynna.game.player.Player
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class DialogueManager(
    private val player: Player,
) {
    var continueContinuation: CancellableContinuation<Unit>? = null
    var optionContinuation: CancellableContinuation<Int>? = null

    fun handleContinue() {
        val cont = continueContinuation ?: return
        continueContinuation = null
        cont.resume(Unit)
    }

    fun handleOption(option: Int) {
        val cont = optionContinuation ?: return
        optionContinuation = null
        continueContinuation = null
        cont.resume(option)
    }

    fun close() {
        val cont = continueContinuation
        val opt = optionContinuation

        continueContinuation = null
        optionContinuation = null

        cont?.cancel()
        opt?.cancel()

        player.interfaceManager.closeChatBoxInterface()
    }
}
