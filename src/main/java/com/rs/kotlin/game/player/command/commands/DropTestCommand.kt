package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.npc.drops.DropTableRegistry.getDropTableForNpc
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments

class DropTestCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Test a npc drop & banks items"
    override val usage = "::droptest <id> <level>"

    override fun execute(player: Player, args: List<String>, trigger: String): Boolean {
        val cmdArgs = CommandArguments(args)

        val npcId = cmdArgs.getInt(0)
        val times = cmdArgs.getInt(1)
        dropTest(player, npcId, times)
        return true
    }

    // Add all collected items to bank
    fun dropTest(player: Player, npcId: Int, times: Int) {
        val table = getDropTableForNpc(npcId)
        if (table == null) {
            player.message("No drop table for NPC ID: $npcId")
            return
        }

        val dropCounts: MutableMap<Int, Int> = HashMap()
        table.writeRatesToFile(Settings.DROP_MULTIPLIER)
        for (i in 0..<times) {
            val drops = table.rollDrops(player)
            for (drop in drops) {
                if (drop == null) continue
                dropCounts.merge(drop.itemId, drop.amount) { a: Int?, b: Int? ->
                    Integer.sum(
                        a!!,
                        b!!
                    )
                }

                if (drop.extraDrop != null) {
                    val extra = drop.extraDrop!!
                    dropCounts.merge(extra.itemId, extra.amount) { a: Int?, b: Int? ->
                        Integer.sum(
                            a!!,
                            b!!
                        )
                    }
                }
            }
        }

        for ((itemId, totalAmount) in dropCounts) {
            player.getBank().addItem(itemId, totalAmount, true)
            player.collectionLog.addItem(Item(itemId, totalAmount))
        }

        player.message("Simulated $times kills of NPC ID $npcId. Drops deposited to bank.")
    }
}
