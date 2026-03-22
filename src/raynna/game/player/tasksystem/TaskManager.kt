package raynna.game.player.tasksystem

import raynna.game.player.Player
import raynna.util.Utils
import java.io.Serializable

class TaskManager : Serializable {
    companion object {
        private const val serialVersionUID = 5402478562684453627L

        val FINISH_MESSAGES =
            listOf(
                "Nice job!",
                "Great job!",
                "Awesome!",
                "Well done!",
                "Cool!",
                "Amazing!",
            )

        private fun finishMessage(): String = FINISH_MESSAGES.random()
    }

    @Transient
    private lateinit var player: Player

    private val completedTasks = mutableSetOf<Task>()
    private val taskStages = mutableMapOf<Task, Int>()

    fun setPlayer(player: Player) {
        this.player = player
    }

    fun resetAllTasks() {
        Task.entries.forEach {
            taskStages[it] = 0
        }
        completedTasks.clear()
        player.packets.sendGameMessage("All tasks reset.")
    }

    fun isCompleted(task: Task) = task in completedTasks

    fun stage(task: Task) = taskStages[task] ?: 0

    fun setStage(
        task: Task,
        value: Int,
    ) {
        if (!isCompleted(task)) {
            taskStages[task] = value
        }
    }

    @JvmOverloads
    fun progress(
        task: Task,
        amount: Int = 1,
    ) {
        if (isCompleted(task)) return

        val newStage = stage(task) + amount

        if (newStage >= task.amount) {
            complete(task)
        } else {
            setStage(task, newStage)
        }

        player.refreshAchievementTab()
    }

    private fun complete(task: Task) {
        completedTasks += task
        taskStages[task] = task.amount

        val message = "${finishMessage()} You completed a ${task.difficulty} task: ${task.name}"

        player.packets.sendFilteredGameMessage(true, message)

        sendMoneyReward(task.difficulty)

        showCompletionOverlay(task)
    }

    private fun showCompletionOverlay(task: Task) {
        player.queue().enqueueSoft {
            player.interfaceManager.sendOverlay(3050, false)
            player.packets.sendTextOnComponent(
                3050,
                6,
                Utils.wrapString(Utils.formatString(task.toString()), 18),
            )
            player.packets.sendRunScript(10003)
        }

        player.queue().enqueueDelay(4)

        player.queue().enqueueSoft {
            player.packets.sendRunScript(10005)
        }

        player.queue().enqueueDelay(2)
        player.packets.sendFilteredGameMessage(
            true,
            (
                (
                    finishMessage() + " You completed a " + Utils.formatString(task.difficulty.name) +
                        " task: " + Utils.formatString(task.name.replace("$", "'")) +
                        "! To view what tasks you got left, check achievement tab."
                )
            ),
        )
        player.adventureLog.addActivity(
            (
                "I have completed a " + Utils.formatString(task.difficulty.name) + " task: " +
                    Utils.formatString(task.name.replace("$", "'"))
            ),
        )
    }

    private fun sendMoneyReward(difficulty: Difficulty) {
        val amount =
            when (difficulty) {
                Difficulty.EASY -> 50_000
                Difficulty.MEDIUM -> 100_000
                Difficulty.HARD -> 150_000
                Difficulty.ELITE -> 250_000
            }

        player.moneyPouch.addMoney(amount, false)
    }

    fun claimRewards() {
        Difficulty.entries.forEach { difficulty ->

            if (!completedAll(difficulty)) return

            if (!previousCompleted(difficulty)) {
                player.packets.sendGameMessage("Complete previous difficulty tasks first.")
                return
            }

            giveRewards(difficulty)
        }
    }

    fun completedAll(difficulty: Difficulty) =
        Task.entries
            .filter { it.difficulty == difficulty }
            .all { isCompleted(it) }

    private fun previousCompleted(difficulty: Difficulty): Boolean {
        val previous = Difficulty.entries.toTypedArray().takeWhile { it != difficulty }

        return previous.all { completedAll(it) }
    }

    private fun giveRewards(difficulty: Difficulty) {
        Reward.entries
            .filter { it.difficulty == difficulty }
            .forEach { reward ->

                if (playerHasItem(reward.itemId)) {
                    player.packets.sendGameMessage("You already have this reward.")
                    return@forEach
                }

                if (player.inventory.hasFreeSlots()) {
                    player.inventory.addItem(reward.itemId, 1)
                } else {
                    player.bank.addItem(reward.itemId, 1, true)
                }
            }
    }

    private fun playerHasItem(itemId: Int): Boolean =
        player.inventory.containsItem(itemId, 1) ||
            player.bank.getItem(itemId) != null ||
            player.equipment.containsOneItem(itemId)

    fun completedAllTasks() = completedTasks.containsAll(Task.entries.toList())
}
