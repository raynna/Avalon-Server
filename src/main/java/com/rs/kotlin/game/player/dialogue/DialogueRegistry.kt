package com.rs.kotlin.game.player.dialogue

import com.rs.kotlin.game.player.queue.QueueTask

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
