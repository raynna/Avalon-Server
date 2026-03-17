package com.rs.kotlin.game.player.dialogue.dialogues

import com.rs.kotlin.game.player.queue.QueueTask

suspend fun QueueTask.bankerdialogue(npcId: Int) {
    npc("Good day. How may I help you?", npcId)
    var running = true
    while (running) {
        when (
            options(
                "I'd like to access my bank account, please.",
                "I'd like to check my PIN settings",
                "I'd like to see my collection box.",
                "What is this place?",
            )
        ) {
            1 -> {
                player("I'd like to access my bank account.")
                player.bank.openBank()
                running = false
            }

            2 -> {
                player("I'd like to check my PIN settings.")
                player.bank.openPinSettings()
                running = false
            }

            3 -> {
                player("I'd like to see my collection box.")
                player.geManager.openCollectionBox()
                running = false
            }

            4 -> {
                player("What is this place?")
                npc("This is a branch of the Bank of Avalon. We have branches in many towns.", npcId)
            }
        }
    }
}
