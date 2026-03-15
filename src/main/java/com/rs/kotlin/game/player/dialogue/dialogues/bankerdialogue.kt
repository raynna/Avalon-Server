package com.rs.kotlin.game.player.dialogue.dialogues

import com.rs.kotlin.game.player.queue.QueueTask

suspend fun QueueTask.bankerdialogue(npcId: Int) {
    chatNpc("Good day. How may I help you?", npcId)

    when (
        options(
            "Access bank",
            "What is this place?",
        )
    ) {
        1 -> {
            chatPlayer("I'd like to access my bank account.")
            player.bank.openBank()
        }

        2 -> {
            chatPlayer("What is this place?")
            chatNpc("This is a branch of the Bank of Gielinor.", npcId)
        }
    }
}
