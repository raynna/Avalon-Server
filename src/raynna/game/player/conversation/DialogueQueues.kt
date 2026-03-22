package raynna.game.player.conversation

import raynna.game.player.Player
import raynna.game.player.queue.QueueTask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DialogueQueues {
    @OptIn(DelicateCoroutinesApi::class)
    @JvmStatic
    fun queueDialogue(
        player: Player,
        block: suspend QueueTask.() -> Unit,
    ) {
        val task = QueueTask(player)
        player.newDialogueManager.close()
        GlobalScope.launch {
            try {
                task.block()
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                player.newDialogueManager.close()
            }
        }
    }
}
