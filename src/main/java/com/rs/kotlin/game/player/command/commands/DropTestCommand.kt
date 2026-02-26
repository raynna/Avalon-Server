package com.rs.kotlin.game.player.command.commands

import com.rs.Settings
import com.rs.core.cache.defintions.NPCDefinitions
import com.rs.java.game.item.Item
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.npc.drops.DropTableRegistry.getDropTableForNpc
import com.rs.kotlin.game.npc.drops.tables.minigame.BarrowsChestTable
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.player.command.CommandArguments

class DropTestCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Test a npc drop & banks items"
    override val usage = "::droptest <id> <level>"

    override fun execute(
        player: Player,
        args: List<String>,
        trigger: String,
    ): Boolean {
        val cmdArgs = CommandArguments(args)
        if (args[0].equals("barrows", true)) {
            val brothersKilled = args.getOrNull(1)?.toIntOrNull() ?: 6
            val times = args.getOrNull(2)?.toIntOrNull() ?: 1

            barrowsDropTest(player, brothersKilled, times)
            return true
        }
        val npcId = cmdArgs.getInt(0)
        val times = cmdArgs.getInt(1)
        dropTest(player, npcId, times)
        return true
    }

    fun dropTest(
        player: Player,
        npcId: Int,
        times: Int,
    ) {
        val table = getDropTableForNpc(npcId)
        if (table == null) {
            player.message("No drop table for NPC ID: $npcId")
            return
        }

        val dropCounts: MutableMap<Int, Int> = HashMap()
        table.writeRatesToFile(Settings.DROP_MULTIPLIER)
        val combatLevel = NPCDefinitions.getNPCDefinitions(npcId).combatLevel
        for (i in 0..<times) {
            player.killcount.increment(npcId)
            val drops = table.rollDrops(player, combatLevel, Settings.DROP_MULTIPLIER)
            for (drop in drops) {
                if (drop == null) continue
                dropCounts.merge(drop.itemId, drop.amount) { a: Int?, b: Int? ->
                    Integer.sum(
                        a!!,
                        b!!,
                    )
                }

                if (drop.extraDrop != null) {
                    val extra = drop.extraDrop!!
                    dropCounts.merge(extra.itemId, extra.amount) { a: Int?, b: Int? ->
                        Integer.sum(
                            a!!,
                            b!!,
                        )
                    }
                }
            }
        }

        for ((itemId, totalAmount) in dropCounts) {
            player.getBank().addItem(itemId, totalAmount, true)
        }
        player.message("Simulated $times kills of NPC ID $npcId. Drops deposited to bank.")
    }

    fun barrowsDropTest(
        player: Player,
        brothersKilled: Int,
        times: Int,
    ) {
        val dropCounts: MutableMap<Int, Int> = HashMap()

        repeat(times) {
            player.killcount.increment("barrows chest")
            // Fake killed brothers array
            val killed =
                BooleanArray(6) { i ->
                    i < brothersKilled
                }

            player.killedBarrowBrothers = killed

            val rolls = 1 + brothersKilled

            repeat(rolls) {
                val denom =
                    BarrowsChestTable.barrowsUniqueChance(brothersKilled)

                BarrowsChestTable.BARROWS_CHEST_TABLE
                    .setPreRollDenominator(denom)

                val drops =
                    BarrowsChestTable.BARROWS_CHEST_TABLE.rollDrops(player, 0, Settings.DROP_MULTIPLIER)

                for (drop in drops) {
                    dropCounts.merge(drop.itemId, drop.amount, Int::plus)

                    if (drop.extraDrop != null) {
                        val extra = drop.extraDrop!!
                        dropCounts.merge(extra.itemId, extra.amount, Int::plus)
                    }
                }
            }

            player.temporaryAttribute().remove("barrows_used")
        }

        for ((itemId, totalAmount) in dropCounts) {
            player.bank.addItem(itemId, totalAmount, true)
            player.collectionLog.addItem(Item(itemId, totalAmount))
        }
        BarrowsChestTable.BARROWS_CHEST_TABLE
            .writeRatesToFile(Settings.DROP_MULTIPLIER)
        player.message(
            "Simulated $times Barrows chests with $brothersKilled brothers killed.",
        )
    }
}
