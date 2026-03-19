package com.rs.kotlin.game.player.dialogue

import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.dialogue.dialogues.bankerdialogue
import com.rs.kotlin.game.player.dialogue.dialogues.levelUp
import com.rs.kotlin.game.player.dialogue.dialogues.mandrith
import com.rs.kotlin.game.player.dialogue.dialogues.shopselect

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

    @JvmStatic
    fun shop(player: Player) {
        DialogueQueues.queueDialogue(player) {
            shopselect()
        }
    }

    @JvmStatic
    fun mandrith(
        player: Player,
        npcId: Int,
    ) {
        DialogueQueues.queueDialogue(player) {
            mandrith(npcId)
        }
    }

    @JvmStatic
    fun levelup(
        player: Player,
        skill: Int,
        oldLevel: Int,
        newLevel: Int,
    ) {
        player.queue().enqueueWeak {
            DialogueQueues.queueDialogue(player) {
                levelUp(skill, oldLevel, newLevel)
            }
        }
    }
}
