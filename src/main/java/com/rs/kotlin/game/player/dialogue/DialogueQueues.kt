package com.rs.kotlin.game.player.dialogue

import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.queue.QueueTask
import kotlinx.coroutines.runBlocking

object DialogueQueues {
    @JvmStatic
    fun queueDialogue(
        player: Player,
        block: suspend QueueTask.() -> Unit,
    ) {
        val task = QueueTask(player)

        runBlocking {
            try {
                task.block()
            } finally {
                player.newDialogueManager.close()
            }
        }
    }
}
