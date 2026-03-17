package com.rs.kotlin.game.player.dialogue.dialogues

import com.rs.kotlin.game.player.queue.QueueTask

suspend fun QueueTask.ellis(npcId: Int) {
    npc("Hello " + player.displayName + ", What can i do for you?", npcId)

    when (
        options(
            "I'd like to tan some dragonhides, please.",
            "I'd like to see what you have to offer!",
            "Nothing, just walking by.",
        )
    ) {
        1 -> {
            player("I'd like to access my bank account.")
            player.bank.openBank()
        }

        2 -> {
            player("What is this place?")
            npc("This is a branch of the Bank of Gielinor.", npcId)
        }
    }
}
