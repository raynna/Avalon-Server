package raynna.game.npc.plugins

import raynna.game.npc.NPC
import raynna.game.npc.NpcPlugin
import raynna.game.player.Player
import raynna.game.player.skills.fishing.Fishing
import raynna.game.player.skills.fishing.SpotDefinition

class FishingSpotPlugin : NpcPlugin() {
    override fun getKeys(): Array<Any> = SpotDefinition.getAllNpcIds().toTypedArray()

    /** Option 1: Net / Lure / Cage */
    override fun processNpc(
        player: Player,
        npc: NPC,
    ): Boolean {
        startFishing(player, npc, option = 1)
        return true
    }

    /** Option 2: Bait / Harpoon */
    override fun processNpc2(
        player: Player,
        npc: NPC,
    ): Boolean {
        startFishing(player, npc, option = 2)
        return true
    }

    private fun startFishing(
        player: Player,
        npc: NPC,
        option: Int,
    ) {
        val spot = SpotDefinition.forNpcId(npc.id, option)
        if (spot == null) {
            println("[FishingSpotPlugin] No spot found for npcId=${npc.id}, option=$option")
            return
        }
        println("[FishingSpotPlugin] Starting fishing: spot=${spot.name}, npc=${npc.id}, option=$option")
        player.actionManager.setAction(Fishing(npc, spot))
    }
}
