package raynna.game.player.conversation.scenes

import raynna.game.player.queue.QueueTask
import raynna.game.player.shop.OpenShopAction

suspend fun QueueTask.shopselect() {
    val displays = OpenShopAction.ShopDisplay.valuesInOrder()

    val ids = displays.map { it.iconItemId }.toIntArray()
    val names = displays.map { it.name.replace("_", " ") }

    val index =
        selection(
            "Click on the alternative stores down below.",
            ids,
            names,
        )

    if (index in displays.indices) {
        val display = displays[index]
        player.actionManager.setAction(
            OpenShopAction(display.shop, 0),
        )
    }

    stop()
}
