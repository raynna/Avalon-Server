package raynna.game.player.conversation

import raynna.game.player.queue.QueueTask

object DialogueRegistry {
    val dialogues = mutableMapOf<Int, suspend QueueTask.(Int) -> Unit>()

    fun register(
        npcId: Int,
        script: suspend QueueTask.(Int) -> Unit,
    ) {
        dialogues[npcId] = script
    }

    fun get(npcId: Int) = dialogues[npcId]
}
