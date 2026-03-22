package raynna.game.player.queue

import raynna.game.player.Player
import java.util.ArrayDeque

class PlayerActionQueue(
    private val player: Player,
) {
    private val queue = ArrayDeque<QueuedAction>()
    private var running: QueuedAction? = null

    fun enqueueWeak(action: Runnable) {
        queue.add(QueuedAction(action, 0, QueueType.WEAK))
    }

    fun enqueueNormal(action: Runnable) {
        queue.add(QueuedAction(action, 0, QueueType.NORMAL))
    }

    fun enqueueStrong(action: Runnable) {
        queue.add(QueuedAction(action, 0, QueueType.STRONG))
    }

    fun enqueueSoft(action: Runnable) {
        queue.add(QueuedAction(action, 0, QueueType.SOFT))
    }

    fun enqueueDelay(
        ticks: Int,
        type: QueueType = QueueType.NORMAL,
    ) {
        queue.add(QueuedAction(null, ticks, type))
    }

    fun process() {
        val current =
            running ?: run {
                if (queue.isEmpty()) return

                val next = queue.first()

                if (next.type == QueueType.STRONG) {
                    closeModalInterfaces()
                    removeWeakScripts()
                }

                if (!canRun(next)) return

                queue.removeFirst()
                running = next
                next
            }

        if (current.delay > 0) {
            current.delay--
            return
        }

        try {
            current.action?.run()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        running = null
    }

    private fun canRun(action: QueuedAction): Boolean {
        if (player.interfaceManager.containsChatBoxInter()) return false
        if (player.interfaceManager.containsScreenInter()) return false
        return true
    }

    private fun removeWeakScripts() {
        queue.removeIf { it.type == QueueType.WEAK }
    }

    private fun closeModalInterfaces() {
        player.interfaceManager.closeChatBoxInterface()
    }

    fun clearWeak() {
        queue.removeIf { it.type == QueueType.WEAK }
    }

    fun clearAll() {
        queue.clear()
        running = null
    }

    private data class QueuedAction(
        val action: Runnable?,
        var delay: Int,
        val type: QueueType,
    )
}
