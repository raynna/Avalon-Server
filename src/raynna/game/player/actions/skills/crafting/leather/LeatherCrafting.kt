package raynna.game.player.actions.skills.crafting.leather

import raynna.core.cache.defintions.ItemDefinitions
import raynna.game.item.Item
import raynna.game.player.Player
import raynna.game.player.Skills
import raynna.game.player.actions.Action

class LeatherCrafting(
    private val data: LeatherData,
    option: Int,
    private var quantity: Int,
) : Action() {
    private val product: LeatherProduct = data.products[option]

    override fun start(player: Player): Boolean = check(player)

    private fun check(player: Player): Boolean {
        if (player.skills.getLevel(Skills.CRAFTING) < product.level) {
            player.message("You need a Crafting level of ${product.level}.")
            return false
        }

        if (!player.hasTool(NORMAL_NEEDLE)) {
            player.message("You don't have a needle to craft with.")
            return false
        }

        if (!player.inventory.containsItem("item.thread", 1)) {
            player.message("You don't have any thread to craft with.")
            return false
        }

        val missing = StringBuilder()

        for (req in product.requirements) {
            val have = player.inventory.getAmountOf(req.getId())

            if (have < req.amount) {
                if (missing.isNotEmpty()) {
                    missing.append(", ")
                }

                missing
                    .append(req.amount)
                    .append(" ")
                    .append(ItemDefinitions.getItemDefinitions(req.getId()).name)
            }
        }

        if (missing.isNotEmpty()) {
            player.message("You need: $missing.")
            return false
        }

        return true
    }

    override fun process(player: Player): Boolean {
        if (quantity <= 0) {
            return false
        }

        player.animate(CRAFT_ANIMATION)

        if (player.skills.getLevel(Skills.CRAFTING) < product.level) {
            player.message("You need a Crafting level of ${product.level} to continue crafting this.")
            return false
        }

        if (!player.inventory.containsItem("item.thread", 1)) {
            player.message("You ran out of thread.")
            return false
        }

        if (product.requirements.isEmpty()) {
            val base = data.getBaseLeather()

            if (!player.inventory.containsItem(base, 1)) {
                val name = ItemDefinitions.getItemDefinitions(base).name
                player.message("You have run out of $name.")
                return false
            }

            return true
        }

        for (req in product.requirements) {
            if (!player.inventory.containsItem(req.getId(), req.amount)) {
                val name = ItemDefinitions.getItemDefinitions(req.getId()).name
                player.message("You have run out of $name.")
                return false
            }
        }

        return true
    }

    override fun processWithDelay(player: Player): Int {
        var crafted = player.temporaryAttributtes["THREAD_CRAFT_PROGRESS"] as? Int ?: 0

        quantity--

        if (product.requirements.isEmpty()) {
            player.inventory.deleteItem(data.getBaseLeather(), 1)
        } else {
            for (req in product.requirements) {
                player.inventory.deleteItem(req.getId(), req.amount)
            }
        }

        crafted++

        if (crafted % 5 == 0) {
            player.inventory.deleteItem(THREAD, 1)
        }

        player.temporaryAttributtes["THREAD_CRAFT_PROGRESS"] = crafted

        player.inventory.addItem(product.getId(), 1)
        player.skills.addXp(Skills.CRAFTING, product.xp)

        return 3
    }

    override fun stop(player: Player) {
        setActionDelay(player, 3)
    }

    companion object {
        const val THREAD = 1734
        const val DUNG_NEEDLE = 17446
        const val NORMAL_NEEDLE = 1733

        private const val CRAFT_ANIMATION = 1249

        fun getLeatherData(itemId: Int): LeatherData? = LeatherData.entries.firstOrNull { it.getBaseLeather() == itemId }

        fun getLeatherData(
            used: Item,
            usedWith: Item,
        ): LeatherData? {
            val id1 = used.id
            val id2 = usedWith.id

            if (id1 == id2) {
                return null
            }

            val hasNeedle =
                id1 == DUNG_NEEDLE || id2 == DUNG_NEEDLE ||
                    id1 == NORMAL_NEEDLE || id2 == NORMAL_NEEDLE

            for (data in LeatherData.entries) {
                val base = data.getBaseLeather()

                if (hasNeedle && (base == id1 || base == id2)) {
                    return data
                }

                for (p in data.products) {
                    var found1 = false
                    var found2 = false

                    for (r in p.requirements) {
                        val rid = r.getId()
                        if (rid == id1) found1 = true
                        if (rid == id2) found2 = true
                    }

                    if (found1 && found2) {
                        return data
                    }
                }
            }

            return null
        }
    }
}
