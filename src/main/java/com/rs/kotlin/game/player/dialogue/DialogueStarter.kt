package com.rs.kotlin.game.player.dialogue

import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.dialogue.DialogueQueues
import com.rs.kotlin.game.player.dialogue.dialogues.bankerdialogue

object DialogueStarter {
    @JvmStatic
    fun banker(
        player: Player,
        npcId: Int,
    ) {
        DialogueQueues.queueDialogue(player) {
            bankerdialogue(npcId)
        }
    }
}
