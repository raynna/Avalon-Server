package com.rs.kotlin.game.player.command.commands

import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command
import com.rs.kotlin.game.world.task.WorldTasks
import com.rs.kotlin.game.world.task.worldTaskCoroutine

class TaskExampleCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "WorldTask examples."
    override val usage = "::taskexample <1-7>"

    override fun execute(
        player: Player,
        args: List<String>,
        trigger: String,
    ): Boolean {
        val example = args.firstOrNull()?.toIntOrNull()
        if (example == null) {
            player.message("Usage: $usage")
            player.message("Examples:")
            player.message("  1 — One-shot, next tick")
            player.message("  2 — One-shot, 3 tick delay")
            player.message("  3 — Repeat every tick, 5 times")
            player.message("  4 — Repeat every 2 ticks, 4 times")
            player.message("  5 — Coroutine: sequential steps with varying delays")
            player.message("  6 — Coroutine: loop 5 times, 1 tick apart")
            player.message("  7 [ticks] [count] — Coroutine: dynamic args")
            return true
        }

        when (example) {
            1 -> {
                player.message("[1] Scheduled one-shot task...")
                WorldTasks.submit {
                    player.message("[1] Fired on next tick. Done.")
                }
            }

            2 -> {
                player.message("[2] Scheduled one-shot with 3 tick delay...")
                WorldTasks.submit(3) {
                    player.message("[2] Fired after 3 ticks. Done.")
                }
            }

            3 -> {
                player.message("[3] Starting repeating task (every tick, 5 times)...")
                var count = 0
                WorldTasks.repeat(1) {
                    count++
                    player.message("[3] Tick $count / 5")
                    if (count >= 5) {
                        player.message("[3] Reached 5. Stopping.")
                        stop()
                    }
                }
            }

            4 -> {
                player.message("[4] Starting repeating task (wait 2 ticks, then every 1 ticks, 4 times)...")
                var count = 0
                WorldTasks.repeat(2, 1) {
                    count++
                    player.message("[4] Execution $count / 4")
                    if (count >= 4) {
                        player.message("[4] Reached 4. Stopping.")
                    }
                    stop()
                }
            }

            5 -> {
                player.message("[5] Starting coroutine task...")
                worldTaskCoroutine {
                    player.message("[5] Step 1 — immediate")
                    delay(3)
                    player.message("[5] Step 2 — 3 ticks later")
                    delay(2)
                    player.message("[5] Step 3 — 2 ticks later")
                    delay(5)
                    player.message("[5] Step 4 — 5 ticks later. Done.")
                }
            }

            6 -> {
                player.message("[6] Starting coroutine loop (5 shots, 1 tick apart)...")
                worldTaskCoroutine {
                    repeat(5) { i ->
                        player.message("[6] Shot ${i + 1} / 5")
                        delay(1)
                    }
                    player.message("[6] All shots fired. Done.")
                }
            }

            7 -> {
                val ticks = args.getOrNull(1)?.toIntOrNull()
                val count = args.getOrNull(2)?.toIntOrNull()
                if (ticks == null || count == null || ticks < 0 || count < 1) {
                    player.message("Usage: ::taskexample 7 [ticks] [count]")
                    return true
                }
                player.message("[7] Firing $count times, $ticks ticks apart...")
                worldTaskCoroutine {
                    repeat(count) { i ->
                        player.message("[7] Execution ${i + 1} / $count")
                        if (i < count - 1) delay(ticks)
                    }
                    player.message("[7] Done.")
                }
            }

            else -> {
                player.message("Unknown example '$example'. Usage: $usage")
            }
        }

        return true
    }
}
