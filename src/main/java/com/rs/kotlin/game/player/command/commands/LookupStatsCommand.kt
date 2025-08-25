package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.World
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.npc.combatdata.CombatData
import com.rs.kotlin.game.player.command.Command

class LookupStatsCommand : Command {
    override val requiredRank = Ranks.Rank.PLAYER
    override val description = "Shows an NPC's combat stats in an interface."
    override val usage = "::lookupstats <npcId>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        if (args.isEmpty()) {
            player.message("Usage: $usage")
            return true
        }

        val npcId = args[0].toIntOrNull()
        if (npcId == null) {
            player.message("Invalid NPC ID.")
            return true
        }
        player.message("Executing LookupStatsCommand for NPC ID: $npcId") // DEBUG

        val npc: NPC = World.getNPC(npcId) ?: run {
            player.message("NPC with ID $npcId not found.")
            return true
        }

        var combatData: CombatData? = npc.combatData
        if (combatData == null) {
            player.message("${npc.name} has no combat data.")
            if (npc.combatData == null) {
                npc.setBonuses()
            }
            combatData = npc.combatData;
        }
        if (combatData == null) {
            player.message("${npc.name} still has no combat data.")
            return true
        }

        // Interface setup
        val interfaceId = 275
        val titleComponentId = 1
        val startLine = 10
        player.interfaceManager.sendInterface(interfaceId)
        player.packets.sendTextOnComponent(interfaceId, titleComponentId, "*${npc.name} Combat Data*")

        var lineId = startLine

        fun sendLine(label: String, value: Any?) {
            player.packets.sendTextOnComponent(interfaceId, lineId, "$label: $value")
            lineId++
        }

        // Basic levels
        sendLine("Combat Level", combatData.combatLevel)
        sendLine("Attack Level", combatData.attackLevel)
        sendLine("Strength Level", combatData.strengthLevel)
        sendLine("Defence Level", combatData.defenceLevel)
        sendLine("Magic Level", combatData.magicLevel)
        sendLine("Ranged Level", combatData.rangedLevel)
        sendLine("Constitution", combatData.constitutionLevel)

        // Bonuses
        sendLine("Attack Bonus", combatData.attackBonus)
        sendLine("Strength Bonus", combatData.strengthBonus)
        sendLine("Magic Bonus", combatData.magicBonus)
        sendLine("Magic Strength Bonus", combatData.magicStrengthBonus)
        sendLine("Ranged Bonus", combatData.rangedBonus)
        sendLine("Ranged Strength Bonus", combatData.rangedStrengthBonus)

        // Defences
        sendLine("Melee Defence (Stab/Slash/Crush)", "${combatData.meleeDefence.stab}/${combatData.meleeDefence.slash}/${combatData.meleeDefence.crush}")
        sendLine("Magic Defence", combatData.magicDefence.magic)
        sendLine("Ranged Defence (Light/Standard/Heavy)", "${combatData.rangedDefence.light}/${combatData.rangedDefence.standard}/${combatData.rangedDefence.heavy}")

        // Other
        sendLine("Aggressive", combatData.aggressive)
        sendLine("Max Hit", combatData.maxHit.maxhit)
        sendLine("Attack Speed Ticks", combatData.attackSpeedTicks)
        sendLine("Respawn Ticks", combatData.respawnTicks)
        sendLine("Slayer XP", combatData.slayerXp)

        // Immunities
        sendLine("Immunities (Poison/Venom/Cannons/Thralls/Burn)",
            "${combatData.immunities.poison}/${combatData.immunities.venom}/${combatData.immunities.cannons}/${combatData.immunities.thralls}/${combatData.immunities.burn}"
        )

        // Clear remaining lines
        for (i in lineId..310) {
            player.packets.sendTextOnComponent(interfaceId, i, "")
        }

        return true
    }
}
