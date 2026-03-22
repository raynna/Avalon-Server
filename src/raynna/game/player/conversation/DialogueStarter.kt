package raynna.game.player.conversation

import raynna.game.player.Player
import raynna.game.player.conversation.scenes.bankerdialogue
import raynna.game.player.conversation.scenes.levelUp
import raynna.game.player.conversation.scenes.mandrith
import raynna.game.player.conversation.scenes.shopselect

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
